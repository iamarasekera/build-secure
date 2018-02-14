package lk.ishara.buildsecure.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import lk.ishara.buildsecure.core.AndroidManifest;
import lk.ishara.buildsecure.core.Constants;
import lk.ishara.buildsecure.core.UsesPermission;
import lk.ishara.buildsecure.core.UsesPermission.eUpStatus;

/**
 * Created by Ishara on 12/25/2017.
 */

public class ManifestParser {

	public static void modifyManifest(String userManifest,
                                               AndroidManifest androidManifest,
                                               boolean allowDataBackup) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setIgnoringElementContentWhitespace(true);

        try {
            DocumentBuilder builder = dbf.newDocumentBuilder();
            InputSource is1 = new InputSource(userManifest);
            Document xmlDoc = builder.parse(is1);
            xmlDoc.setXmlStandalone(true);
            System.out.println("adjust");
			adjustNodes(androidManifest, xmlDoc, allowDataBackup);
            Node root = xmlDoc.getDocumentElement();
            root.normalize();

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            FileOutputStream fos = new FileOutputStream(userManifest);
            transformer.transform(new DOMSource(xmlDoc), new StreamResult(new OutputStreamWriter(fos, "UTF-8")));
            fos.close();
        } catch (SAXException e) {
            System.exit(1);
        } catch (ParserConfigurationException e) {
            System.err.println(e);
            System.exit(1);
        } catch (IOException e) {
            System.err.println(e);
            System.exit(1);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    public static AndroidManifest parse(String userManifest) {
        AndroidManifest androidManifest = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setIgnoringElementContentWhitespace(true);

        try {
            DocumentBuilder builder = dbf.newDocumentBuilder();
            InputSource is1 = new InputSource(userManifest);
            Document xmlDoc = builder.parse(is1);
            xmlDoc.setXmlStandalone(true);

            androidManifest = getManifest(xmlDoc);
        } catch (SAXException e) {
            System.exit(1);
        } catch (ParserConfigurationException e) {
            System.err.println(e);
            System.exit(1);
        } catch (IOException e) {
            System.err.println(e);
            System.exit(1);
        }
        return androidManifest;
    }

    private static AndroidManifest getManifest(Document doc) {
        AndroidManifest androidManifest = new AndroidManifest();
        System.out.println("getManifest");
        String pkgName = doc.getDocumentElement().getAttribute("package");
        androidManifest.setPackageName(pkgName);
        String projectName = Util.getProjectName();

		Node appNode = doc.getElementsByTagName(Constants.APPLICATION).item(0);

		if (appNode.getNodeType() == Node.ELEMENT_NODE) {
			Element eElement = (Element) appNode;

                if (projectName == null || projectName.isEmpty()) {
                    projectName = eElement.getAttribute(Constants.ANDROID_LABEL);
                }
                androidManifest.setProjectName(projectName);

                boolean allowBackUp = Boolean.parseBoolean(eElement.getAttribute(Constants.ANDROID_ALLOW_BKUP));
                androidManifest.setAllowBackup(allowBackUp);
            }

        NodeList nList = doc.getElementsByTagName(Constants.USES_PERMISSION);

        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                String permItem = eElement.getAttribute(Constants.ANDROID_NAME);

                UsesPermission up = new UsesPermission();
                up.setPermissionName(permItem);
                up.setUpStatus(eUpStatus.upS_existed);
                androidManifest.getUsesPermissions().add(up);
            }
        }
        return androidManifest;
    }
    /**
     * @param doc
	 * @param disableDataBackup
	 *            - new value of data backup to be exported
     */
 	private static void adjustNodes(AndroidManifest androidManifest, Document doc,
			boolean disableDataBackup) {
		// List of elements to be 
		List<Element> elementsToRemove = new ArrayList<>();
		
		// process all USES_PERMISION list
		NodeList nList = doc.getElementsByTagName(Constants.USES_PERMISSION);

		for (int nodeItem = 0; nodeItem < nList.getLength(); nodeItem++) {
			Node nNode = nList.item(nodeItem);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				String permName = eElement.getAttribute(Constants.ANDROID_NAME);

				for (UsesPermission up : androidManifest.getUsesPermissions()){
					if (up.getPermissionName().equals(permName) &&
						up.getUpStatus().equals(UsesPermission.eUpStatus.upS_removed)) {
					elementsToRemove.add(eElement);
					}
				} 
			}
		}

		// remove all uses-permission elements listed in elementsToRemove list
		for (Element el : elementsToRemove) {
			el.getParentNode().removeChild(el);
		}

		Node applicationNode = doc.getElementsByTagName(Constants.APPLICATION).item(0);
		if (applicationNode.getNodeType() == Node.ELEMENT_NODE) {
			Element applicationElement = (Element) applicationNode;
			/**
			 * process application element and update android:allowBackup
			 * only if it configured as allow_data_backup=false
			 */
			if (disableDataBackup) {
				applicationElement.setAttribute(Constants.ANDROID_ALLOW_BKUP, "false");
			}
		
			for (UsesPermission up : androidManifest.getUsesPermissions()){
				if (up.getUpStatus().equals(UsesPermission.eUpStatus.upS_added)){
			Element newElement = doc.createElement(Constants.USES_PERMISSION);
					newElement.setAttribute(Constants.ANDROID_NAME, up.getPermissionName());
			Node newNode = doc.adoptNode(newElement);
					// adding new uses permissions before application element
					doc.getDocumentElement().insertBefore(newNode, applicationElement);
				}
			}
		}
	}
}
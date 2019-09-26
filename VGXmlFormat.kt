package vgTools

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Text
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import java.io.StringWriter
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory


class VGXmlFormat<T : Any> {

    private val factory = DocumentBuilderFactory.newInstance()
    private val builder = factory.newDocumentBuilder()
    private val doc = builder.newDocument()
    private var nameSpaceElement: Element? = null

    @Throws(TransformerException::class, ParserConfigurationException::class)
    fun buildXml(nameSpace: String = "mapper", defaultNamespaceUri: String = "", data: T? = null): String {
        doc.xmlStandalone = true

        doc.buildNameSpace(nameSpace = nameSpace, defaultNamespaceUri = defaultNamespaceUri)

        if (data != null) nameSpaceElement?.appendChildrenFromObject(data = data)

        return writeXml().toString()
    }

//    private fun Document.addXMLSheet() {
//        val xmlSheet = doc.createProcessingInstruction("xml-sheet", "type='text/xsl' href='myfile.xslt'")
//        doc.appendChild(xmlSheet)
//    }

    private fun Document.buildNameSpace(nameSpace: String, defaultNamespaceUri: String) {
        nameSpaceElement = doc.createElement(nameSpace)
        nameSpaceElement?.setAttribute("xmlns", defaultNamespaceUri)
        this.appendChild(nameSpaceElement)
    }

    private fun Element.appendChildrenFromObject(data: T?) {
        data?.foreachProperty { key, value, _, _ ->
            this.appendChild(doc.createElement(key).appendText(value = value))
        }
    }

    private fun Element.appendText(value: Any?): Element {
        this.appendChild(createTextNode(value = value))
        return this
    }

    private fun createTextNode(value: Any?): Text {
        return doc.createTextNode(value.toString())
    }

//    private fun buildCDATA(string: String): CDATASection {
//        return doc.createCDATASection(string)
//    }

    private fun writeXml(): StringWriter {
        val transformerFactory = TransformerFactory.newInstance()
        val transformer = transformerFactory.newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.setOutputProperty(OutputKeys.VERSION, "1.0")
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8")
        val writer = StringWriter()
        val streamResult = StreamResult(writer)
        val source = DOMSource(doc)
        transformer.transform(source, streamResult)
        return writer
    }

}

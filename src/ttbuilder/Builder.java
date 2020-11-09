package ttbuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.SwingWorker;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author david
 */
public class Builder {

    private String[] infilespec;
    private String outfilespec;

    ArrayList<ClsDef> Defs;

    private File FXmlFile;
    private Document doc;

    private String RecordDelim = String.valueOf('\0');
    private String ComponentDelim = ";";
    private String ItemDelim = ",";
    private BufferedWriter outfile;
    private int repnum;

    public Builder() {
        this.Defs = new ArrayList();
    }

    public void ProcessArgs(String args[]) {
        //CommandLine line ;
        CommandLineParser parser;
        Options options = new Options();
        Option o;

        o = new Option("i", true, "Input File");
        o.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(o);

        options.addOption("o", true, "Output File");
        options.addOption("t", false, "Text File");
        parser = new DefaultParser();

        try {
            // parse the command line arguments

            CommandLine line = parser.parse(options, args);
            if (line.hasOption("t")) {
                RecordDelim = String.valueOf('\n');  //+String.valueOf('\r');   
            }

            if (line.hasOption("o")) {
                outfilespec = line.getOptionValue("o");

                System.out.println("Output file " + outfilespec);

                try {
                    outfile = new BufferedWriter(new FileWriter(outfilespec));
                } catch (IOException ex) {
                    Logger.getLogger(Builder.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

            if (line.hasOption("i")) {
                infilespec = line.getOptionValues("i");
                for (String filespec : infilespec) {

                    if (filespec != null) {
                        File f = new File(filespec);
                        if (f.exists()) {
                            FXmlFile = f;
                            loadfile(f);

                        }
                    }
                }
            }

            outfile.flush();
            outfile.close();

        } catch (ParseException exp) {
            // oops, something went wrong
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
                    }
          catch (IOException exp){
              System.err.println("IO Exception.  Reason: " + exp.getMessage());
          }
    }

    public void ProcessFiles() {

    }

    public void ReadFile(File fXmlFile) {

        this.FXmlFile = fXmlFile;

        SwingWorker loader = new SwingWorker<Void, Void>() {
            @Override
            public Void doInBackground() {
                //loadfile();
                return null;
            }

                @Override
            public void done() {

            }
        };

        //loadfile();
        //loader.execute();
    }

    private void loadfile(File xmlFile) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(xmlFile);
            Node DocRootNode;
            Node DocNode;
            Element DefsElement;
            Element DefElement;
            NodeList GroupsList;
            Element ServiceElement;
            Element StartTimeElement;
            System.out.println("Reading " + xmlFile.getName());
            WritetoFile("*" + xmlFile.getName() + RecordDelim);
            doc.getDocumentElement().normalize();
            DocRootNode = (Node) doc.getDocumentElement(); //   doc.getElementsByTagName("TimeTable").item(0);
            for (int i = 0; i < DocRootNode.getChildNodes().getLength(); i++) {
                DocNode = DocRootNode.getChildNodes().item(i);
                switch (DocNode.getNodeName()) {
                    case ("Group"): {
                        parsegroup(DocNode);
                        break;
                    }
                    case ("Defs"): {
                        parsedefs(DocNode);
                        break;
                    }
                    case ("TimeTableStart"): {
                        StartTimeElement = (Element) DocNode;
                        if (StartTimeElement != null) {
                            //System.out.println(getElementString(StartTimeElement));
                            ClsTime t;
                            t = parseTimeString(getElementString(StartTimeElement));
                            WritetoFile(t.toString() + RecordDelim);
                        }

                        break;
                    }

                }

            }

            //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Builder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(Builder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Builder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void parsedefs(Node DefsNode) {
        NodeList DefsList;

        if (DefsNode != null) {
            DefsList = ((Element) DefsNode).getElementsByTagName("Def");
            for (int temp = 0; temp < DefsList.getLength(); temp++) {
                Node nNode = DefsList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    ClsDef newDef = new ClsDef();
                    newDef.Name = getElementAttribute((Element) nNode, "Name");
                    newDef.Data = getElementAttribute((Element) nNode, "Data");
                    if (!"".equals(newDef.Name) && !"".equals(newDef.Data)) {
                        Defs.add(newDef);
                    }
                }
            }
        }
    }

    void parsegroup(Node GroupNode) {

        Node GroupItem;
        Element GroupElement;
        String GroupInfo;

        if (GroupNode == null || GroupNode.getNodeType() != Node.ELEMENT_NODE) {
            return;
        }

        GroupElement = (Element) GroupNode;
        GroupInfo = getElementAttribute(GroupElement, "Info");
        if (!GroupInfo.equals("")) {
            WritetoFile("*<" + GroupInfo + ">" + RecordDelim);
        }

        for (int i = 0; i < GroupElement.getChildNodes().getLength(); i++) {
            if (GroupElement.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
                GroupItem = GroupElement.getChildNodes().item(i);
                //System.out.println(GroupItem.getNodeName());
                switch (GroupItem.getNodeName()) {
                    case "Group": {
                        parsegroup(GroupItem);
                        break;
                    }
                    case "Service": {
                        parseService(GroupItem);
                        break;
                    }
                    case "Comment": {
                        WritetoFile("*" + GroupItem.getTextContent() + RecordDelim);
                        break;
                    }
                }
            }

        }
        if (!GroupInfo.equals("")) {
            WritetoFile("*</" + GroupInfo + ">" + RecordDelim);
        }
    }

    void parseService(Node ServiceNode) {
        NodeList ServiceElements;
        NodeList ServiceTimes;

        String ServiceTime;

        repnum = 1;

        ServiceElements = ((Element) ServiceNode).getElementsByTagName("Element");
        //System.out.println(ServiceElements.getLength());
        ServiceTimes = ((Element) ServiceNode).getElementsByTagName("Starttime");

        if (ServiceTimes.getLength() == 0) {
            writeServiceHeader((Element) ServiceNode, 0);
            parseElements(ServiceElements, parseTimeString("00:00"));
        } else {
            for (int i = 0; i < ServiceTimes.getLength(); i++) {
                ServiceTime = ServiceTimes.item(i).getTextContent();
                //System.out.println("Offset " + parseTimeString(ServiceTime));
                writeServiceHeader((Element) ServiceNode, repnum);
                parseElements(ServiceElements, parseTimeString(ServiceTime));
                repnum++;
            }
        }

    }

    void writeServiceHeader(Element ServiceElement, int rep) {
        String ServiceEquipment;
        String ID;
        String Desc;

        ServiceEquipment = getElementString((Element) ServiceElement, "Equipment");
        ID = getElementAttribute((Element) ServiceElement, "id");
        Desc = getElementAttribute((Element) ServiceElement, "Desc");

        WritetoFile(((0 == rep) ? "" : rep + "-") + ID + ";" + Desc);
        if ("".equals(ServiceEquipment)) {
        } else {
            WritetoFile(";" + ServiceEquipment);
        }

    }

    void parseElements(NodeList ServiceElements, ClsTime OffsetTime) {
        Element E;
        Element EItem;
        ClsTime tDate;

        for (int i = 0; i < ServiceElements.getLength(); i++) {
            E = (Element) ServiceElements.item(i);
            WritetoFile(ItemDelim);

            for (int j = 0; j < E.getChildNodes().getLength(); j++) {
                if (E.getChildNodes().item(j).getNodeType() == Node.ELEMENT_NODE) {
                    EItem = (Element) E.getChildNodes().item(j);

                    switch (EItem.getNodeName()) {
                        case "Time": {
                            tDate = parseTimeString(EItem.getTextContent());
                            tDate.add(OffsetTime);

                            WritetoFile(tDate.toString() + ComponentDelim);
                            //OffsetTime.
                            break;
                        }
                        case "Data": {

                            WritetoFile(EItem.getTextContent());
                            break;

                        }
                    }
                }
            }

        }
        WritetoFile(RecordDelim);
    }

    private String getElementString(Element eElement, String ElementText) {

        NodeList n = eElement.getElementsByTagName(ElementText);
        if (n == null) {
            return "";
        }
        if (n.getLength() == 0) {
            return "";
        }
        return n.item(0).getTextContent();
    }

    private String getElementString(Element eElement) {

        if (eElement != null) {
            return eElement.getTextContent();
        } else {
            return "";
        }
    }

    private int getElementInt(Element eElement, String ElementText) {

        NodeList n = eElement.getElementsByTagName(ElementText);
        if (n == null) {
            return 0;
        }
        if (n.getLength() == 0) {
            return 0;
        }
        return Integer.valueOf(n.item(0).getTextContent());
    }

    ClsTime parseTimeString(String TimeString) {

        return new ClsTime(TimeString);
    }

    String getElementAttribute(Element El, String Attr) {
        if (El == null) {
            return "";
        }
        return ((Element) El).getAttribute(Attr);
    }

    void WritetoFile(String txt) {
        //String tstring = txt;
        String OutString = txt;
        if (!"".equals(txt)) {
            OutString = OutString.replaceAll(java.util.regex.Pattern.quote("%ID%"), Integer.toString(repnum));
            OutString = OutString.replaceAll(java.util.regex.Pattern.quote("%ID++%"), Integer.toString(repnum + 1));
            OutString = OutString.replaceAll(java.util.regex.Pattern.quote("%ID--%"), Integer.toString(repnum - 1));
            try {
                for (ClsDef d : Defs) {
                    //System.out.println("Checking " + tstring + " for " + d.Name);
                    OutString = OutString.replaceAll(java.util.regex.Pattern.quote(d.Name), d.Data);
                    //tstring = OutString;
                }
                outfile.write(OutString);
                System.out.println(OutString);
            } catch (IOException ex) {
                Logger.getLogger(Builder.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
}

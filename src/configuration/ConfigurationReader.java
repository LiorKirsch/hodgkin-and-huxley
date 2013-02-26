package configuration;

import neuron.NeuronalGroup;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ConfigurationReader {
    public static NeuronalGroup[] getGroups(String configFileName) {
        
        //
        // Create an instance of SAXBuilder
        //
        SAXBuilder builder = new SAXBuilder();
        NeuronalGroup[] groups = null;
        
        try {
            //
            // Tell the SAXBuilder to build the Document object from the InputStream
            // supplied.
            //
        	File configFile = new File(configFileName);
            Document document = builder.build(configFile);
            
            //
            // Get our xml documen root element which equals to the <root> tag in
            // the xml document.
            //
            Element root = document.getRootElement();

            //
            // Get all the children named with <row> in the document. The method
            // return the children as a java.util.List object.
            //
            
            	Element groupsElement = (Element)root.getChildren("groups").get(0);
                List columns = groupsElement.getChildren("group");
                groups = new NeuronalGroup[columns.size()];
                for (int j = 0; j < columns.size(); j++) {
                	Element group = (Element) columns.get(j);
                    String name = group.getAttribute("name").getValue();
                    int neuronsInGroup = group.getAttribute("neuronsInGroup").getIntValue();
                    groups[j] = new NeuronalGroup(neuronsInGroup,name);
                    groups[j].groupName= name;
                }
                Element connectionElement = (Element)root.getChildren("connections").get(0);
                List columns2 = connectionElement.getChildren("connection");
                
                for (int j = 0; j < columns2.size(); j++) {
                	Element connection = (Element) columns2.get(j);
                    String source = connection.getAttribute("source").getValue();
                    String output = connection.getAttribute("output").getValue();
                    connectGroups(groups,source,output);
                }
            
                
            
             
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
        	System.out.println("an IO exception has occured");
            e.printStackTrace();
        }
        return groups;
    }
    
    private static void connectGroups(NeuronalGroup[] groups, String sourceGroupName, String outputGroupName)
    {
    	NeuronalGroup sourceGroup =null;
    	NeuronalGroup outputGroup =null;
    	int i=0;
    	int j=0;
    	while (sourceGroup ==null & i< groups.length)
    	{
    		if (  sourceGroupName.equalsIgnoreCase(groups[i].groupName) )
    			sourceGroup=groups[i];
    		i++;
    	}
    	if (sourceGroup != null)
    	{
    		while (outputGroup ==null & j< groups.length)
        	{
        		if (  outputGroupName.equalsIgnoreCase(groups[j].groupName) )
        			outputGroup=groups[j];
        		j++;
        	}
    		
    		if( outputGroup!=null)
    			outputGroup.getInputFromGroup(sourceGroup,15, true, 0.05);
    	}
    	
    }
}

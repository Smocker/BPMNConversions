package org.processmining.plugins.converters.bpmn2pn;

import javax.swing.JOptionPane;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.Progress;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;

/**
 * Conversion of a BPMN model to Petri nets, only considering the control-flow of the model. 
 *
 * @author Dirk Fahland
 * Jul 18, 2013
 */
@Plugin(name = "Convert BPMN diagram to Petri net (control-flow)", parameterLabels = { "Petri net" }, 
returnLabels = { "Petri net", "Initial Marking"}, returnTypes = { Petrinet.class, Marking.class }, 
userAccessible = true, help = "Convert BPMN diagram to Petri net")
public class BPMN2PetriNetConverter_Plugin {
	
	@UITopiaVariant(affiliation = "Università di Pisa",  author = "Daniele Cicciarella",  email = "cicciarellad@gmail.com")
	@PluginVariant(variantLabel = "Convert BPMN diagram to Petri net", requiredParameterLabels = { 0 })
	public Object[] convert(PluginContext context, BPMNDiagram bpmn) {
		
		BPMN2PetriNetConverter conv = new BPMN2PetriNetConverter(bpmn);
		
		Progress progress = context.getProgress();
		progress.setCaption("Converting BPMN diagram to Petri net");
		
		boolean success = conv.convert();
		
		if (success) {
			Petrinet net = conv.getPetriNet();
			Marking m = conv.getMarking();
			context.getConnectionManager().addConnection(new InitialMarkingConnection(net, m));

			if (!conv.getWarnings().isEmpty())
				showWarningsandErrors(context, conv);
			
			context.getFutureResult(0).setLabel("Petri net from "+bpmn.getLabel());
			context.getFutureResult(1).setLabel("Initial marking of the PN from "+bpmn.getLabel());
		
			return new Object[] {net, m};
		} else {
			if (!conv.getErrors().isEmpty() || !conv.getWarnings().isEmpty())
				showWarningsandErrors(context, conv);
			
			return cancel(context, "Could not translate BPMN diagram");
		}
	}
	
	private void showWarningsandErrors(PluginContext context, BPMN2PetriNetConverter conv) {
		StringBuffer warnings = new StringBuffer();
		for (String error : conv.getErrors()) {
			warnings.append("Error: "+error); warnings.append('\n');
		}
		for (String warning : conv.getWarnings()) {
			warnings.append("Warning: "+warning); warnings.append('\n');
		}
		showMessage(context, warnings.toString());
	}
	
	private void showMessage(PluginContext context, String message) {
		if (context instanceof UIPluginContext) {
			JOptionPane.showMessageDialog(null, message, "BPMN2PetriNet conversion", JOptionPane.WARNING_MESSAGE);
		} else {
			System.out.println(message);
			context.log(message);
		}
	}
	
	public static Object[] cancel(PluginContext context, String message) {
		System.out.println("[BPMN2PetriNet]: "+message);
		context.log(message);
		context.getFutureResult(0).cancel(true);
		context.getFutureResult(1).cancel(true);
		return null;
	}
}

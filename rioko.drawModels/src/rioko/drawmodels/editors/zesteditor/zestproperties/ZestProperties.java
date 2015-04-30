package rioko.drawmodels.editors.zesteditor.zestproperties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

import org.eclipse.zest.layouts.LayoutAlgorithm;

import rioko.utilities.Copiable;
import rioko.utilities.Log;
import rioko.utilities.Pair;
import rioko.graphabstraction.algorithms.NestedBuilderAlgorithm;
import rioko.graphabstraction.configurations.BadArgumentException;
import rioko.graphabstraction.configurations.BadConfigurationException;
import rioko.graphabstraction.configurations.Configuration;
import rioko.graphabstraction.diagram.DiagramGraph;
import rioko.graphabstraction.diagram.DiagramNode;
import rioko.graphabstraction.display.DisplayOptions;
import rioko.graphabstraction.display.FilterNestedBuilder;
import rioko.drawmodels.algorithms.display.JustFiltersBuilder;
import rioko.drawmodels.diagram.ModelDiagram;
import rioko.drawmodels.editors.zesteditor.ZestEditor;
import rioko.drawmodels.events.PropertiesChangeEvent;

public class ZestProperties implements Copiable {
	
	//Constantes de la clase
	private static final int MAX_NODES_DEFAULT = 4;
	
	public static final String STR_LAYOUT_ALG = "Layout Algorithm";
	
	public static final String STR_AGGREGATION_ALG = "Visualization Criteria";
	
	public static final String STR_SHOW_ATTR = "Show attributes";
	
	public static final String STR_SHOW_CON = "Show connections";
	
	//Configuraciones
	private ZestGenericConfigurable genericConf = new ZestGenericConfigurable();
	
	private ZestAlgorithmConfigurable algorithmConf = new ZestAlgorithmConfigurable();
	
	//Filtros posteriores
	private NestedBuilderAlgorithm postAlgorithmFilters = new JustFiltersBuilder();

	//Pila de estados
	private Stack<Pair<NestedBuilderAlgorithm, ZestAlgorithmConfigurable>> stackView = new Stack<>();

	//Builders
	public ZestProperties()
	{
		this(null, MAX_NODES_DEFAULT);
	}
	
	public ZestProperties(DiagramNode root)
	{
		this(root, MAX_NODES_DEFAULT);
	}
	
	public ZestProperties(int maxNodes)
	{
		this(null,maxNodes);
	}
	
	public ZestProperties(DiagramNode root, int maxNodes)
	{
		try {
			this.genericConf.setConfiguration(ZestGenericProperties.SHOW_ATTR, true);
			this.genericConf.setConfiguration(ZestGenericProperties.SHOW_CON, false);
		} catch (BadConfigurationException | BadArgumentException e) {
			// Impossible Exception
			e.printStackTrace();
		}

		try {
			this.algorithmConf.setConfiguration(DisplayOptions.ROOT_NODE, root);
			this.algorithmConf.setConfiguration(DisplayOptions.MAX_NODES, maxNodes);
		} catch (BadConfigurationException | BadArgumentException e) {
			// Impossible Exception
			e.printStackTrace();
		}
	}
	
	//Getters & Setters	
	public ZestGenericConfigurable getGenericConfigurable() {
		return this.genericConf;
	}
	
	public ZestAlgorithmConfigurable getAlgorithmConfigurable() {
		this.algorithmConf.setAlgorithm(this.getActualNestedAlgorithm());
		return this.algorithmConf;
	}
	
	public LayoutAlgorithm getActualLayout() {
		return (LayoutAlgorithm) this.genericConf.getConfiguration(ZestGenericProperties.LAYOUT_ALG);
	}
	
	public NestedBuilderAlgorithm getActualNestedAlgorithm() {
		return (NestedBuilderAlgorithm) this.genericConf.getConfiguration(ZestGenericProperties.AGGREGATION_ALG);
	}
		
	public boolean isShowingData() {
		return (Boolean)this.genericConf.getConfiguration(ZestGenericProperties.SHOW_ATTR);
	}

	public void setShowingData(boolean showingData) {
		try {
			this.genericConf.setConfiguration(ZestGenericProperties.SHOW_ATTR, showingData);
		} catch (BadConfigurationException | BadArgumentException e) {
			// Impossible exception
			e.printStackTrace();
		}
		
		this.hasChanged(ZestEditor.UPDATE_NODES);
	}

	public boolean isShowingConnectionLabels() {
		return (Boolean)this.genericConf.getConfiguration(ZestGenericProperties.SHOW_CON);
	}

	public void setShowingConnectionLabels(boolean showingConnectionLabels) {
		try {
			this.genericConf.setConfiguration(ZestGenericProperties.SHOW_CON, showingConnectionLabels);
		} catch (BadConfigurationException | BadArgumentException e) {
			// Impossible exception
			e.printStackTrace();
		}
		
		this.hasChanged(ZestEditor.UPDATE_CONNECTIONS);
	}

	public DiagramNode getRootNode() {
		return (DiagramNode) this.algorithmConf.getConfiguration(DisplayOptions.ROOT_NODE);
	}

	public void setRootNode(DiagramNode rootNode) {
		this.stackConfiguration();
		
		try {
			this.algorithmConf.setConfiguration(DisplayOptions.ROOT_NODE, rootNode);
		} catch (BadConfigurationException | BadArgumentException e) {
			// Impossible Exception
			e.printStackTrace();
		}
		
		this.hasChanged(ZestEditor.UPDATE_ALL);
	}

	public int getMaxNodes() {
		return (int) this.algorithmConf.getConfiguration(DisplayOptions.MAX_NODES);
	}

	public void setMaxNodes(int maxNodes) {
		this.stackConfiguration();
		
		try {
			this.algorithmConf.setConfiguration(DisplayOptions.MAX_NODES, maxNodes);
		} catch (BadConfigurationException | BadArgumentException e) {
			// Impossible Exception
			e.printStackTrace();
		}

		this.hasChanged(ZestEditor.UPDATE_ALL);
	}
	
	//Auxiliary Layout methods
	
	public void changeLayout(LayoutAlgorithm layout)
	{
		try {
			this.genericConf.setConfiguration(ZestGenericProperties.LAYOUT_ALG, layout);
		} catch (BadConfigurationException e) {
			// Excepción por error en layout
			Log.exception(e);
		} catch (BadArgumentException e) {
			// Exception por error al configurar
			Log.exception(e);
		}
	}
	
	public void changeLayout(String layoutName)
	{
		try {
			this.genericConf.setConfiguration(ZestGenericProperties.LAYOUT_ALG, layoutName);
		} catch (BadConfigurationException e) {
			// Excepción por error en layout
			Log.exception(e);
		} catch (BadArgumentException e) {
			// Exception por error al configurar
			Log.exception(e);
		}
	}
	
	//Auxiliary Nested Algorithms methods
	public void addNewNestedAlgorithm(NestedBuilderAlgorithm algorithm) {
		this.genericConf.addAggregationAlgorithm(algorithm);
		
		this.changeNestedAlgorithm(algorithm);
	}
	
	public void changeNestedAlgorithm(NestedBuilderAlgorithm builder)
	{
		this.stackConfiguration();
		
		try {
			this.genericConf.setConfiguration(ZestGenericProperties.AGGREGATION_ALG, builder);
		} catch (BadConfigurationException e) {
			// Excepción por error en layout
			Log.exception(e);
		} catch (BadArgumentException e) {
			// Exception por error al configurar
			Log.exception(e);
		}
	}
	
	public void changeNestedAlgorithm(String builderName)
	{
		this.stackConfiguration();
		
		try {
			this.genericConf.setConfiguration(ZestGenericProperties.AGGREGATION_ALG, builderName);
		} catch (BadConfigurationException e) {
			// Excepción por error en algoritmo
			Log.exception(e);
		} catch (BadArgumentException e) {
			// Exception por error al configurar
			Log.exception(e);
		}
	}
	
	//Auxiliary Filters control methods
	private boolean addFilterToPostFilters(FilterNestedBuilder filter) {
		if(filter.checkProperties()) {
			return this.postAlgorithmFilters.addFilter(filter);
		}
		
		return false;
	}
	
	public boolean addAllFilterToPostFilters(Collection<FilterNestedBuilder> collection) {
		boolean res = false;
		
		for(FilterNestedBuilder filter : collection) {
			res |= this.addFilterToPostFilters(filter);
		}
		
		if(res) {
			this.hasChanged(ZestEditor.UPDATE_ALL);
		}
		
		return res;
	}
	
	public boolean removeFilterFromPostFilters(FilterNestedBuilder filter) {
		return this.postAlgorithmFilters.removeFilter(filter);
	}
	
	public void removeAllFilterFromPostFilters() {
		this.postAlgorithmFilters.removeAllFilters();
	}
	
	public DiagramGraph applyFilters(DiagramGraph graph) {
		return this.postAlgorithmFilters.createNestedGraph(graph, this.algorithmConf);
	}
	
	//Get tables Data mathods	
	public void hasChanged(int changes) {
		new PropertiesChangeEvent(this, changes);
	}
	
	//Building methods
	public DiagramGraph getDrawGraph(ModelDiagram model) {
		DiagramGraph printable = this.applyFilters(this.getActualNestedAlgorithm().createNestedGraph(model.getModelDiagram(), this.algorithmConf));
		model.setPrintDiagram(printable);
		
		return printable;
	}
	
	//Methods to manage the stack
	public void stackConfiguration()
	{
		this.stackView.add(new Pair<>(this.getActualNestedAlgorithm(), this.algorithmConf.copy()));
	}
	
	public boolean popConfiguration()
	{
		if(!this.stackView.isEmpty()) {
			Pair<NestedBuilderAlgorithm, ZestAlgorithmConfigurable> pair = this.stackView.pop();
			
			this.changeNestedAlgorithm(pair.getFirst());
			this.algorithmConf.put(pair.getLast());
			
			this.hasChanged(ZestEditor.UPDATE_ALL);
			
			return true;
		}
		
		return false;
	}

	//Copaible methods
	@Override
	public ZestProperties copy() {
		ZestProperties res = new ZestProperties();

		Collection<Configuration> conf = new ArrayList<Configuration>();
		for(Pair<String,Configuration> pair : this.genericConf.getConfiguration()) {
			conf.add(pair.getLast().copy());
		}
		
		try {
			res.genericConf.setConfiguration(conf);
		} catch (BadConfigurationException | BadArgumentException e) {
			//Exception
			e.printStackTrace();
		}

		res.algorithmConf = this.algorithmConf.copy();

		//Propiedades de visualización
		Stack<Pair<NestedBuilderAlgorithm, ZestAlgorithmConfigurable>> auxStack = new Stack<>();
		res.stackView.clear();
		while(!this.stackView.isEmpty()) {
			auxStack.push(this.stackView.pop());
		}
		
		while(!auxStack.isEmpty()) {
			Pair<NestedBuilderAlgorithm, ZestAlgorithmConfigurable> element = auxStack.pop();
			this.stackView.push(element);
			res.stackView.push(element);
		}
		
		return res;
	}
}

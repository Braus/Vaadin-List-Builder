package eu.abeel.platform.rwa.portal.components.standard;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.templatemodel.TemplateModel;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import eu.abeel.platform.rwa.portal.Listeners.ValueChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A Designer generated component for the list-builder-component.html template.
 *
 * list-builder component as advertised on the website, doesn't look a 100% the same.
 * This is because the grid cannot be pre-initialized otherwise it throws a type error.
 * The list can be initialized with any type of object.
 * 
 */
@Tag("list-builder-component")
//set the path correct, this was the path of my project
@HtmlImport("components/standard/list-builder-component.html")
public class ListBuilderComponent extends PolymerTemplate<ListBuilderComponent.ListBuilderComponentModel> {

    //the four default buttons you see
    @Id("moveAllEntitiesLeft")
    private Button moveAllEntitiesLeft;
    @Id("moveEntityLeft")
    private Button moveEntityLeft;
    @Id("moveAllEntitiesRight")
    private Button moveAllEntitiesRight;
    @Id("moveEntityRight")
    private Button moveEntityRight;
    
    //These are the components shown on the left
    private String titleLeft;
    private Grid leftGrid;
    private Set leftEntities;
    
    //these are the items shown on the right
    private String titleRight;
    private Grid rightGrid;
    private Set rightEntities;
    
    //The type used for the grids
    private Class type;
    
    //the parents to which the grids and possible titles are appended to
    @Id("leftGridParent")
    private Div leftGridParent;
    @Id("rightGridParent")
    private Div rightGridParent;

    //The colums of the grid, could be improved by making it seperate for the left and right grid
    //But that wasn't necessery for my use case, feel free to improve
    private String[] columns;

    List<ValueChangeListener> listeners = new ArrayList<ValueChangeListener>();
    
    /**
     * default initialization of buttons
     */
    public ListBuilderComponent(){
        moveEntityRight.addClickListener(e -> {moveEntityRight();valueChanged();});
        moveAllEntitiesRight.addClickListener(e -> {moveAllEntitiesRight();valueChanged();});
        moveEntityLeft.addClickListener(e -> {moveEntityLeft();valueChanged();});
        moveAllEntitiesLeft.addClickListener(e -> {moveAllEntitiesLeft();valueChanged();});
        
    }
    
    private void valueChanged(){
        listeners.forEach(listener -> listener.valueChanged());
    }
    
    public void addValueChangeListener(ValueChangeListener listener){
        listeners.add(listener);
    }
    /**
     * This one also sets the titles for left and right, if no titles are specified they won't be generated
     * You might want to swap Set with List as Set is unordered, this wasn't required for my use case
     */
    public void initBuilderComponent(Set itemsLeft, String titelLeft , Set itemsRight, String titelRight){
        this.titleLeft=titelLeft;
        this.titleRight=titelRight;
        initBuilderComponent(itemsLeft,itemsRight);
    }
    
    
    /**
     * Creates type new ListBuilderComponent.
     */
    public void initBuilderComponent(Set itemsLeft, Set itemsRight)
    {
        //here the type of the grids is checked. at least one set should contain an item.
        if(itemsLeft.size() != 0){
            Iterator it = itemsLeft.iterator();
            type = it.next().getClass();
        } else if(itemsRight.size() != 0){
            Iterator it = itemsRight.iterator();
            type = it.next().getClass();
        } else {
            return;
        }
        
        //safe remove operation, list could already be initialized so we remove just to be sure.
        leftGridParent.removeAll();
        rightGridParent.removeAll();

       
        leftEntities = itemsLeft;
        rightEntities = itemsRight;
        setGrid(leftEntities, "left");
        setGrid(rightEntities, "right");
    }
    
        public void initBuilderComponent(List itemsLeft, String titelLeft , List itemsRight, String titelRight){
        initBuilderComponent(new HashSet(itemsLeft), titelLeft , new HashSet(itemsRight), titelRight);
    }
    
    void initBuilderComponent(List itemsLeft, List itemsRight) {
        initBuilderComponent(new HashSet(itemsLeft), new HashSet(itemsRight));
    }
    
    public void setColumns(String... columns){
        this.columns = columns;
        if(leftGrid ==null){
            return;
        }
        leftGrid.setColumns(columns);
        rightGrid.setColumns(columns);
    }
    

    /**
     * here the actual grid is made. as one can see I set a maxheight, this could be done by a method to make it nicer.
     * Should also be possible to set the selectionmode in a method, but again wasn't needed for my use case
     */
    private void setGrid(Set items, String leftOrRight){
        Grid grid =new Grid<>(type);
        grid.setItems(items);
        grid.setMaxHeight("270px");
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        if(columns != null && columns.length != 0 ){
            grid.setColumns(columns);
        }
        if(leftOrRight.equals("left")){
            if(!titleLeft.isEmpty()){
                leftGridParent.add(CreateTitle(titleLeft));
            }
            leftGrid = grid;
            leftGridParent.add(leftGrid);
        } else {
            if(!titleRight.isEmpty()){
                rightGridParent.add(CreateTitle(titleRight));
            }
            rightGrid = grid;
            rightGridParent.add(rightGrid);
        }
    }
    
    private H2 CreateTitle(String titlevalue){
        H2 title = new H2(titlevalue);
        title.getStyle().set("text-align", "center ");
        title.getStyle().set("border-bottom", "1px solid black");
        return title;
    }

    private void moveEntityLeft() {
        Set selected  = rightGrid.getSelectedItems();
        leftEntities.addAll(selected);
        leftGrid.setItems(leftEntities);
        
        rightEntities.removeAll(selected);
        rightGrid.setItems(rightEntities);
    }

    private void moveEntityRight() {
        Set selected  = leftGrid.getSelectedItems();
        rightEntities.addAll(selected);
        rightGrid.setItems(rightEntities);
        leftEntities.removeAll(selected);
        leftGrid.setItems(leftEntities);
    }

    private void moveAllEntitiesRight() {
        Set allEntitiesLeft = new HashSet();
        Iterator it = leftEntities.iterator();
        while(it.hasNext()){
            allEntitiesLeft.add(it.next());
        }
        rightEntities.addAll(allEntitiesLeft);
        rightGrid.setItems(rightEntities);
        leftEntities = new HashSet();
        leftGrid.setItems(leftEntities);
    }

    public Set getLeftEntities() {
        return leftEntities;
    }

    public Set getRightEntities() {
        return rightEntities;
    }

    private void moveAllEntitiesLeft() {
        Set allEntitiesRight = new HashSet();
        Iterator it = rightEntities.iterator();
        while(it.hasNext()){
            allEntitiesRight.add(it.next());
        }
        leftEntities.addAll(allEntitiesRight);
        leftGrid.setItems(leftEntities);
        rightEntities = new HashSet();
        rightGrid.setItems(rightEntities);
    }


    /**
     * This model binds properties between ListBuilderComponent and list-builder-component.html
     */
    public interface ListBuilderComponentModel extends TemplateModel {
        // Add setters and getters for template properties here.
    }
}

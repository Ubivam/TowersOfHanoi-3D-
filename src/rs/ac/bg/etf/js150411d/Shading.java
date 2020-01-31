package rs.ac.bg.etf.js150411d;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;


public class Shading {
    public static final Shading WOOD, CHARCOAL, GOLD, PLASTIC;

    private  static  final PhongMaterial DEFAULT_SELECTED_MATERIAL = new PhongMaterial(Color.AQUAMARINE);
    private  static final PhongMaterial DEFAULT_SELECTABLE_MATERIAL = new PhongMaterial(Color.LIGHTGREEN);
    private static final PhongMaterial DEFAULT_UNSELECTABLE_MATERIAL = new PhongMaterial(Color.INDIANRED);

    private static final String RESOURCES_PATH_PREFIX = "resources/textures/";
    static {
        PhongMaterial woodMaterial = new PhongMaterial();
        woodMaterial.setDiffuseMap(new Image(Thread.currentThread().getContextClassLoader().getResource(RESOURCES_PATH_PREFIX + "wood/TexturesCom_RoughWoodPlanks_2x2_1K_albedo.png").toString()));
        woodMaterial.setBumpMap(new Image(Thread.currentThread().getContextClassLoader().getResource(RESOURCES_PATH_PREFIX + "wood/TexturesCom_RoughWoodPlanks_2x2_1K_normal.png").toString()));
        WOOD = new Shading(woodMaterial, "Wood");

        PhongMaterial charcoalMaterial = new PhongMaterial();
        charcoalMaterial.setDiffuseMap(new Image(Thread.currentThread().getContextClassLoader().getResource(RESOURCES_PATH_PREFIX + "charcoal/TexturesCom_CharredWood_1.2x1.2_1K_albedo.png").toString()));
        charcoalMaterial.setBumpMap(new Image(Thread.currentThread().getContextClassLoader().getResource(RESOURCES_PATH_PREFIX + "charcoal/TexturesCom_CharredWood_1.2x1.2_1K_normal.png").toString()));
        CHARCOAL = new Shading(charcoalMaterial, "Charcoal");

        PhongMaterial goldMaterial = new PhongMaterial();
        goldMaterial.setDiffuseMap(new Image(Thread.currentThread().getContextClassLoader().getResource(RESOURCES_PATH_PREFIX + "gold/TexturesCom_Plastic_SpaceBlanketFolds_1K_albedo.png").toString()));
        goldMaterial.setBumpMap(new Image(Thread.currentThread().getContextClassLoader().getResource(RESOURCES_PATH_PREFIX + "gold/TexturesCom_Plastic_SpaceBlanketFolds_1K_normal.png").toString()));
        GOLD = new Shading(goldMaterial, "Gold");

        PhongMaterial plasticMaterial = new PhongMaterial();
        plasticMaterial.setDiffuseMap(new Image(Thread.currentThread().getContextClassLoader().getResource(RESOURCES_PATH_PREFIX + "plastic/TexturesCom_Pavement_Tactile3_1K_albedo.png").toString()));
        plasticMaterial.setBumpMap(new Image(Thread.currentThread().getContextClassLoader().getResource(RESOURCES_PATH_PREFIX + "plastic/TexturesCom_Pavement_Tactile3_1K_normal.png").toString()));
        PLASTIC = new Shading(plasticMaterial, "Plastic");
    }
    private final PhongMaterial material;
    private final PhongMaterial selectedMaterial;
    private final PhongMaterial selectableMaterial;
    private final PhongMaterial unselectableMaterial;
    private final String name;

    private Shading(PhongMaterial material, PhongMaterial selectedMaterial, PhongMaterial selectableMaterial, PhongMaterial unselectableMaterial, String name){
        this.material = material;
        this.selectedMaterial = selectedMaterial;
        this.selectableMaterial = selectableMaterial;
        this.unselectableMaterial = unselectableMaterial;
        this.name = name;
    }
    private Shading(PhongMaterial material, String name){
        this(material,DEFAULT_SELECTED_MATERIAL,DEFAULT_SELECTABLE_MATERIAL,DEFAULT_UNSELECTABLE_MATERIAL,name);
    }

    public void setSpecularPower(double specularPower){
        material.setSpecularPower(specularPower);
        selectedMaterial.setSpecularPower(specularPower);
    }
    public void setSpecularColor(Color color){
        material.setSpecularColor(color);
        selectedMaterial.setSpecularColor(color);
    }

    public Material getSelectedMaterial() {
        return selectedMaterial;
    }

    public Material getMaterial() {
        return material;
    }

    public Material getUnselectableMaterial() {
        return unselectableMaterial;
    }

    public Material getSelectableMaterial(){ return selectableMaterial; }

    @Override
    public String toString() {
        return name;
    }
}

package rs.ac.bg.etf.js150411d.gameobjects;

import rs.ac.bg.etf.js150411d.Shading;
import rs.ac.bg.etf.js150411d.shapes.DiskMesh;


public class Disk extends DiskMesh implements Selectable {

    private Rod rod;
    private Shading material;
    private boolean selected;

    public Disk(double innerRadius, double outerRadius, float hegiht) {
        super(innerRadius, outerRadius, hegiht);
        this.setOnMouseEntered((event -> {
            if (rod.getHanoi().getInteractionEnabled()) {
                if (isSelectable()) {
                    if (!selected) {
                        setMaterial(material.getSelectableMaterial());
                    }
                } else {
                    setMaterial(material.getUnselectableMaterial());
                }

            }
        }));
        this.setOnMouseExited((event -> {
            if (rod.getHanoi().getInteractionEnabled()) {
                if (!selected) {
                    setMaterial(material.getMaterial());
                } else {
                    setMaterial(material.getSelectedMaterial());
                }
            }
        }));
    }

    public void setRod(Rod rod) {
        this.rod = rod;
    }

    public Rod getRod() {
        return rod;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            setMaterial(material.getSelectedMaterial());
        } else {
            setMaterial(material.getMaterial());
        }
    }

    public void setShading(Shading material) {
        this.material = material;
        if (!selected) {
            setMaterial(material.getMaterial());
        }
    }

    public boolean isSelectable() {
        return rod != null && rod.peek() == this;
    }
}

package io.korti.mrc.item;

import io.korti.mrc.circuit.ComponentType;
import net.minecraft.world.item.Item;

public class ComponentCellItem extends Item {

    private final ComponentType componentType;

    public ComponentCellItem(ComponentType componentType, Properties properties) {
        super(properties);
        this.componentType = componentType;
    }

    public ComponentType getComponentType() {
        return componentType;
    }
}

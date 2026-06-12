package com.megatrex4.client.menu;

import com.bibireden.playerex.components.PlayerEXComponents;
import com.bibireden.playerex.components.player.IPlayerDataComponent;
import com.bibireden.playerex.ui.components.AttributeComponent;
import com.bibireden.playerex.ui.components.MenuComponent;
import com.megatrex4.playerex.PlayerEXInventoryWeightAttributes;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.VerticalAlignment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public final class CarryingMenuComponent extends MenuComponent {

    public CarryingMenuComponent() {
        super(
                Sizing.fill(100),
                Sizing.fill(100),
                FlowLayout.Algorithm.VERTICAL
        );
    }

    @Override
    public void build(FlowLayout screenRoot) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null) {
            return;
        }

        PlayerEntity player = client.player;
        IPlayerDataComponent playerData = PlayerEXComponents.PLAYER_DATA.get(player);

        FlowLayout content = Containers.verticalFlow(
                Sizing.fill(100),
                Sizing.fill(100)
        );

        content.padding(Insets.both(8, 8));
        content.gap(8);
        content.horizontalAlignment(HorizontalAlignment.CENTER);
        content.verticalAlignment(VerticalAlignment.TOP);

        content.child(
                Components.label(Text.translatable("playerex_inventoryweight.ui.carrying.title"))
                        .color(Color.ofArgb(0xFFFFFFFF))
        );

        content.child(
                Components.label(Text.translatable("playerex_inventoryweight.ui.carrying.description"))
                        .color(Color.ofArgb(0x99FFFFFF))
        );

        /*
         * AttributeButtonComponent searches upward for a TextBoxComponent with id "input".
         * This matches PlayerEX's own attributes menu behavior.
         */
        FlowLayout amountRow = Containers.horizontalFlow(
                Sizing.fill(100),
                Sizing.content()
        );

        amountRow.verticalAlignment(VerticalAlignment.CENTER);
        amountRow.horizontalAlignment(HorizontalAlignment.CENTER);
        amountRow.gap(6);

        amountRow.child(
                Components.label(Text.translatable("playerex_inventoryweight.ui.carrying.amount"))
                        .color(Color.ofArgb(0xCCFFFFFF))
        );

        TextBoxComponent input = Components.textBox(Sizing.fixed(32));
        input.text("1");
        input.setMaxLength(3);
        input.setTextPredicate(value -> {
            if (value == null || value.isBlank()) {
                return true;
            }

            try {
                return Integer.parseInt(value) >= 0;
            } catch (NumberFormatException ignored) {
                return false;
            }
        });
        input.verticalSizing(Sizing.fixed(15));
        input.id("input");

        amountRow.child(input);

        content.child(amountRow);

        content.child(
                Components.box(Sizing.fill(100), Sizing.fixed(2))
                        .color(Color.ofArgb(0x22FFFFFF))
        );

        content.child(
                new AttributeComponent(
                        PlayerEXInventoryWeightAttributes.CARRYING,
                        player,
                        playerData
                )
        );

        screenRoot.child(content);
    }
}
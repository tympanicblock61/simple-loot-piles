package net.tympanic.loots.screens;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tympanic.loots.block.LootPile;

public class LootGenScreen extends Screen {
    private final BlockState state;
    private final BlockPos pos;
    private final World world;
    private CheckboxWidget respawn;
    private ButtonWidget tier;

    public LootGenScreen(Text title, BlockState state, BlockPos pos, World world) {
        super(title);
        this.state = state;
        this.pos = pos;
        this.world = world;
    }

    public void init() {
        super.init();
        respawn = this.addDrawableChild(new CheckboxWidget((this.width / 2)-25, 100, 50, 20, Text.of("Respawn"), state.get(LootPile.Respawn)));
        tier = this.addDrawableChild(new ButtonWidget.Builder(Text.of("Change Tier by 1"), button -> {
            if (state.get(LootPile.Tier) >= 2) {
                world.setBlockState(pos, state.with(LootPile.Tier, 0));
            } else {
                world.setBlockState(pos, state.with(LootPile.Tier, state.get(LootPile.Tier) + 1));
            }
        }).dimensions((this.width/2)-50, 120, 100, 20).build());
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        drawCenteredTextWithShadow(matrices, this.textRenderer, this.title, this.width / 2, 15, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (respawn.isMouseOver(mouseX, mouseY)) {
                respawn.onPress();
                MinecraftClient.getInstance().player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1, 1);
                world.setBlockState(pos, state.with(LootPile.Respawn, respawn.isChecked()));
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
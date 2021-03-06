package netherchest.client.renderers;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import netherchest.common.Config;
import netherchest.common.inventory.ExtendedItemStack;

public class RenderItemExtended {

	RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();
	protected static final RenderItemExtended INSTANCE = new RenderItemExtended();

	public static RenderItemExtended instance() {
		return INSTANCE;
	}

	public void setZLevel(float z) {
		itemRender.zLevel = z;
	}

	public float getZLevel() {
		return itemRender.zLevel;
	}

	public void modifyZLevel(float amount) {
		itemRender.zLevel += amount;
	}

	public void renderItemOverlayIntoGUI(FontRenderer fr, ExtendedItemStack stack, int xPosition, int yPosition,
			@Nullable String text) {
		if (!stack.isEmpty()) {

			if (stack.getTopStack().getItem().showDurabilityBar(stack.getTopStack())) {
				GlStateManager.disableLighting();
				GlStateManager.disableDepth();
				GlStateManager.disableTexture2D();
				GlStateManager.disableAlpha();
				GlStateManager.disableBlend();
				Tessellator tessellator = Tessellator.getInstance();
				VertexBuffer vertexbuffer = tessellator.getBuffer();
				double health = stack.getTopStack().getItem().getDurabilityForDisplay(stack.getTopStack());
				int rgbfordisplay = stack.getTopStack().getItem().getRGBDurabilityForDisplay(stack.getTopStack());
				int i = Math.round(13.0F - (float) health * 13.0F);
				int j = rgbfordisplay;
				this.draw(vertexbuffer, xPosition + 2, yPosition + 13, 13, 2, 0, 0, 0, 255);
				this.draw(vertexbuffer, xPosition + 2, yPosition + 13, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255);
				GlStateManager.enableBlend();
				GlStateManager.enableAlpha();
				GlStateManager.enableTexture2D();
				GlStateManager.enableLighting();
				GlStateManager.enableDepth();
			}
			
			if (stack.getCount() != 1 || text != null) {
				String s = text == null ? String.valueOf(stack.getCount()) : text;
				GlStateManager.disableLighting();
				GlStateManager.disableDepth();
				GlStateManager.disableBlend();
				GlStateManager.pushMatrix();
				float scale = Config.TEXT_SCALE;
				GlStateManager.scale(scale, scale, 1.0F);
				fr.drawStringWithShadow(s, (float) (xPosition + 19 - 2 - (fr.getStringWidth(s)*scale))/scale,
						(float) (yPosition + 6 + 3 + (1 / (scale * scale) - 1) )/scale, 16777215);
				GlStateManager.popMatrix();
				GlStateManager.enableLighting();
				GlStateManager.enableDepth();
				// Fixes opaque cooldown overlay a bit lower
				// TODO: check if enabled blending still screws things up down
				// the line.
				GlStateManager.enableBlend();
			}

			EntityPlayerSP entityplayersp = Minecraft.getMinecraft().player;
			float f3 = entityplayersp == null ? 0.0F
					: entityplayersp.getCooldownTracker().getCooldown(stack.getTopStack().getItem(),
							Minecraft.getMinecraft().getRenderPartialTicks());

			if (f3 > 0.0F) {
				GlStateManager.disableLighting();
				GlStateManager.disableDepth();
				GlStateManager.disableTexture2D();
				Tessellator tessellator1 = Tessellator.getInstance();
				VertexBuffer vertexbuffer1 = tessellator1.getBuffer();
				this.draw(vertexbuffer1, xPosition, yPosition + MathHelper.floor(16.0F * (1.0F - f3)), 16,
						MathHelper.ceil(16.0F * f3), 255, 255, 255, 127);
				GlStateManager.enableTexture2D();
				GlStateManager.enableLighting();
				GlStateManager.enableDepth();
			}
		}
	}

	private void draw(VertexBuffer renderer, int x, int y, int width, int height, int red, int green, int blue,
			int alpha) {
		renderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		renderer.pos((double) (x + 0), (double) (y + 0), 0.0D).color(red, green, blue, alpha).endVertex();
		renderer.pos((double) (x + 0), (double) (y + height), 0.0D).color(red, green, blue, alpha).endVertex();
		renderer.pos((double) (x + width), (double) (y + height), 0.0D).color(red, green, blue, alpha).endVertex();
		renderer.pos((double) (x + width), (double) (y + 0), 0.0D).color(red, green, blue, alpha).endVertex();
		Tessellator.getInstance().draw();
	}

}

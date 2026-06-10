package com.shinypings.hud;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;

/**
 * Looks like a vanilla advancement toast, but with a custom header. Vanilla's AdvancementToast
 * hard-codes its header from the advancement type, so we implement Toast directly instead.
 */
public class SpottedToast implements Toast {

	private static final Identifier BACKGROUND_SPRITE = Identifier.withDefaultNamespace("toast/advancement");
	private static final long DISPLAY_TIME_MS = 5000L;

	// Shared token so only one spotted toast exists at a time (see ToastManager#getToast).
	public static final Object TOKEN = new Object();

	private ItemStack icon;
	private Component header;
	private Component title;
	private SoundEvent sound;

	private Toast.Visibility visibility = Toast.Visibility.SHOW;
	private long firstUpdate = -1L;

	public SpottedToast(ItemStack icon, Component header, Component title, SoundEvent sound) {
		this.icon = icon;
		this.header = header;
		this.title = title;
		this.sound = sound;
	}

	@Override
	public Toast.Visibility getWantedVisibility() {
		return this.visibility;
	}

	@Override
	public void update(ToastManager manager, long time) {
		if (this.firstUpdate < 0L) {
			this.firstUpdate = time;
		}
		double shownFor = DISPLAY_TIME_MS * manager.getNotificationDisplayTimeMultiplier();
		this.visibility = (time - this.firstUpdate >= shownFor)
				? Toast.Visibility.HIDE
				: Toast.Visibility.SHOW;
	}

	@Override
	public SoundEvent getSoundEvent() {
		return this.sound;
	}

	@Override
	public Object getToken() {
		return TOKEN;
	}

	// Swap the content and restart the timer so a fresh ping reuses this single toast.
	public void refresh(ItemStack icon, Component header, Component title, SoundEvent sound) {
		this.icon = icon;
		this.header = header;
		this.title = title;
		this.sound = sound;
		this.firstUpdate = -1L;
		this.visibility = Toast.Visibility.SHOW;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, Font font, long time) {
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_SPRITE, 0, 0, this.width(), this.height());
		graphics.text(font, this.header, 30, 7, 0xFFFFFF00);
		graphics.text(font, this.title, 30, 18, 0xFFFFFFFF);
		graphics.item(this.icon, 8, 8);
	}
}

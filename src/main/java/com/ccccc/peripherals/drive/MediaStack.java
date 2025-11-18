package com.ccccc.peripherals.drive;

// SPDX-FileCopyrightText: 2022 The CC: Tweaked Developers
//
// SPDX-License-Identifier: MPL-2.0

import dan200.computercraft.api.media.IMedia;
import dan200.computercraft.shared.platform.PlatformHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

/**
 * An immutable snapshot of the current disk. This allows us to read the stack in a thread-safe manner.
 *
 * @param stack An immutable {@link ItemStack}.
 * @param media The associated {@link IMedia} instance for this stack.
 */
record MediaStack(ItemStack stack, @Nullable IMedia media) {
    static final MediaStack EMPTY = new MediaStack(ItemStack.EMPTY, null);

    static MediaStack of(ItemStack stack) {
        if (stack.isEmpty()) return EMPTY;

        var freshStack = stack.copy();
        return new MediaStack(freshStack, PlatformHelper.get().getMedia(freshStack));
    }

    @Nullable
    SoundEvent getAudio() {
        return media != null ? media.getAudio(stack) : null;
    }

    @Nullable
    String getAudioTitle() {
        return media != null ? media.getAudioTitle(stack) : null;
    }
}
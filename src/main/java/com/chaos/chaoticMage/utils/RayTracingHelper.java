package com.chaos.chaoticMage.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class RayTracingHelper {
	public static final RayTracingHelper INSTANCE = new RayTracingHelper();
    private RayTraceResult target = null;
    private Minecraft mc = Minecraft.getInstance();

    private RayTracingHelper() {
    }

    public void fire() {
        if (mc.objectMouseOver != null) {
            this.target = mc.objectMouseOver;
            return;
        }

        Entity viewpoint = mc.player;
        if (viewpoint == null)
            return;

        this.target = this.rayTrace(viewpoint, mc.playerController.getBlockReachDistance(), 1);
    }

    public RayTraceResult getTarget() {
        return this.target;
    }

    public RayTraceResult rayTrace(Entity entity, double playerReach, float partialTicks) {
        Vec3d eyePosition = entity.getEyePosition(partialTicks);
        Vec3d lookVector = entity.getLook(partialTicks);
        Vec3d traceEnd = eyePosition.add(lookVector.x * playerReach, lookVector.y * playerReach, lookVector.z * playerReach);
        RayTraceContext context = new RayTraceContext(traceEnd, eyePosition, BlockMode.OUTLINE, FluidMode.NONE, entity);
        return mc.world.rayTraceBlocks(context);
    }
}

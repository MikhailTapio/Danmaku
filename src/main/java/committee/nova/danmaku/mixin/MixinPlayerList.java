package committee.nova.danmaku.mixin;

import committee.nova.danmaku.utils.DanmakuManager$;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class MixinPlayerList {
    @Inject(method = "playerLoggedIn", at = @At("TAIL"))
    public void onLoggedIn(EntityPlayerMP playerIn, CallbackInfo ci) {
        DanmakuManager$.MODULE$.openDanmaku();
    }

    @Inject(method = "playerLoggedOut", at = @At("TAIL"))
    public void onLoggedOut(EntityPlayerMP playerIn, CallbackInfo ci) {
        DanmakuManager$.MODULE$.closeDanmaku();
    }
}

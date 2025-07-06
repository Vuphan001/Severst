package anticope.rejects.modules;

import anticope.rejects.MeteorRejectsAddon;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;

public class AntiGrim extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> log = sgGeneral.add(new BoolSetting.Builder()
            .name("log")
            .description("Logs when Grim anticheat packet detected/cancelled.")
            .defaultValue(false)
            .build()
    );

    public AntiGrim() {
        super(MeteorRejectsAddon.CATEGORY, "anti-grim", "Attempts to bypass or cancel packets related to Grim anticheat checks.");
    }

    @EventHandler
    private void onPacketSend(PacketEvent.Send event) {
        // Cancel movement packets sent too frequently or oddly (Grim movement check)
        if (event.packet instanceof PlayerMoveC2SPacket) {
            // Có thể thêm logic nâng cao hơn tại đây (ví dụ: delay, chỉnh sửa data,...)
            cancel(event, "Movement");
        }
        // Cancel hand swing packets (Grim checks autoclicker)
        else if (event.packet instanceof HandSwingC2SPacket) {
            cancel(event, "HandSwing");
        }
        // Cancel sprint/sneak actions (Grim checks for instant actions)
        else if (event.packet instanceof ClientCommandC2SPacket) {
            cancel(event, "ClientCommand");
        }
    }

    @EventHandler
    private void onPacketReceive(PacketEvent.Receive event) {
        // Cancel player position correction (Grim dùng để kiểm tra fly/speed)
        if (event.packet instanceof PlayerPositionLookS2CPacket) {
            cancel(event, "PositionLook");
        }
        // Cancel velocity/explosion packets (Grim kiểm tra KB, anti-knockback, v.v.)
        else if (event.packet instanceof EntityVelocityUpdateS2CPacket || event.packet instanceof ExplosionS2CPacket) {
            cancel(event, "Velocity/Explosion");
        }
        // Có thể thêm các packet khác mà Grim thường sử dụng ở đây
    }

    private void cancel(PacketEvent<?> event, String type) {
        if (log.get()) warning("Cancelled packet: " + type + " (possible Grim check)");
        event.cancel();
    }
}
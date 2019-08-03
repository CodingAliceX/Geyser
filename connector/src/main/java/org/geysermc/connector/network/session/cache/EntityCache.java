/*
 * Copyright (c) 2019 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author GeyserMC
 * @link https://github.com/GeyserMC/Geyser
 */

package org.geysermc.connector.network.session.cache;

import com.flowpowered.math.vector.Vector3f;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnMobPacket;
import lombok.Getter;
import org.geysermc.api.Geyser;
import org.geysermc.connector.console.GeyserLogger;
import org.geysermc.connector.entity.Entity;
import org.geysermc.connector.entity.type.EntityType;
import org.geysermc.connector.network.session.GeyserSession;
import org.geysermc.connector.utils.EntityUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

// Each session has its own EntityCache in the occasion that an entity packet is sent specifically
// for that player (e.g. seeing vanished players from /vanish)
public class EntityCache {

    private GeyserSession session;

    @Getter
    private Map<Long, Entity> entities = new HashMap<Long, Entity>();

    private AtomicLong nextEntityId = new AtomicLong(2L);

    public EntityCache(GeyserSession session) {
        this.session = session;
    }

    public Entity spawnEntity(ServerSpawnMobPacket packet) {
        EntityType type = EntityUtils.toBedrockEntity(packet.getType());
        if (type == null) {
            GeyserLogger.DEFAULT.warning("Mob " + packet.getType() + " is not supported yet!");
            return null;
        }

        Vector3f position = new Vector3f(packet.getX(), packet.getY(), packet.getZ());
        Vector3f motion = new Vector3f(packet.getMotionX(), packet.getMotionY(), packet.getMotionZ());
        Vector3f rotation = new Vector3f(packet.getPitch(), packet.getYaw(), packet.getHeadYaw());

        Entity entity = new Entity(packet.getEntityId(), nextEntityId.incrementAndGet(), type, position, motion, rotation);
        entity.moveAbsolute(position, packet.getPitch(), packet.getYaw());
        return entities.put(entity.getGeyserId(), entity);
    }
}

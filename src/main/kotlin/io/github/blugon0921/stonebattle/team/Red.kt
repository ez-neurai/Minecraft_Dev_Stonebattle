package io.github.blugon0921.stonebattle.team

import io.github.blugon0921.stonebattle.StoneBattle.Companion.team_info
import io.github.blugon0921.stonebattle.StoneBattle.Companion.yaml
import io.github.blugon0921.stonebattle.team.Blue.Companion.blueTeam
import net.kyori.adventure.text.Component
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.server.ServerLoadEvent

class Red : Listener {

    companion object {
        val redTeam = ArrayList<Player>()

        var red_join = Location(Bukkit.getWorld("world"), -192.0, 74.0, 79.0)
        var red_core = Location(Bukkit.getWorld("world"), 77.0, 81.0, 184.0)
        var red_spawn = Location(Bukkit.getWorld("world"), 77.5, 77.0, 174.5, 180.0f, 0.0f)

        val red_bar = Bukkit.createBossBar("RED", BarColor.RED, BarStyle.SEGMENTED_10)
    }


    @EventHandler
    fun onEnable(event : ServerLoadEvent) {
        if(!team_info.exists()) {
            yaml.set("red.join", Location(Bukkit.getWorld("world")!!, -192.0, 74.0, 79.0))
            yaml.set("red.core", Location(Bukkit.getWorld("world")!!, 77.0, 81.0, 184.0))
            yaml.set("red.spawn", Location(Bukkit.getWorld("world")!!, 77.5, 77.0, 174.5, 180.0f, 0.0f))
        }
        red_join = yaml.getLocation("red.join")!!
        red_core = yaml.getLocation("red.core")!!
        red_spawn = yaml.getLocation("red.spawn")!!

        red_bar.progress = 1.0
    }

    @EventHandler
    fun joinRedTeam(event : PlayerMoveEvent) {
        val player = event.player

        for(nbp in red_join.getNearbyEntities(0.5, 0.5, 0.5)) {
            if(nbp !is Player) return

            if(blueTeam.contains(nbp)) blueTeam.remove(nbp)
            if(!redTeam.contains(nbp)) redTeam.add(nbp)
            nbp.sendActionBar(Component.text("${ChatColor.RED}Red Team 설정 완료"))
        }
    }
}
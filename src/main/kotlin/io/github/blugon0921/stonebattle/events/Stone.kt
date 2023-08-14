package io.github.blugon0921.stonebattle.events

import io.github.blugon0921.stonebattle.StoneBattle
import io.github.blugon0921.stonebattle.team.Blue.Companion.blueTeam
import io.github.blugon0921.stonebattle.team.Blue.Companion.blue_bar
import io.github.blugon0921.stonebattle.team.Blue.Companion.blue_core
import io.github.blugon0921.stonebattle.team.Red.Companion.redTeam
import io.github.blugon0921.stonebattle.team.Red.Companion.red_bar
import io.github.blugon0921.stonebattle.team.Red.Companion.red_core
import net.kyori.adventure.text.Component
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Snowball
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import net.md_5.bungee.api.ChatColor
import org.bukkit.entity.Fireball
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.entity.Projectile
import org.bukkit.boss.BossBar
import org.bukkit.Material
import org.bukkit.entity.Minecart
import java.util.*
import kotlin.random.Random


class Stone : Listener {

    // 오브젝트를 던졌을 때의 이벤트를 관리합니다.
    @EventHandler
    fun launchCompactStone(event: PlayerInteractEvent) {
        val player = event.player
        val world = player.world
        val location = player.eyeLocation
        val eventItem = player.inventory.itemInMainHand

        if (player.gameMode == GameMode.SPECTATOR) return
        if (event.action != Action.LEFT_CLICK_AIR && event.action != Action.LEFT_CLICK_BLOCK) return

        // 조약돌
        // 들고 있는 아이템 타입이 조약돌일 경우
        if (eventItem.type == Material.COBBLESTONE) {
            // 재사용 대기시간
            if (player.getCooldown(Material.COBBLESTONE) != 0) return

            // 사운드를 설정합니다.
            world.playSound(location, Sound.ENTITY_ARROW_SHOOT, 0.5f, 0.5f)

            // 변수 설정 및 특성을 할당합니다.
            val stone = world.spawn(location, Snowball::class.java)
            stone.shooter = player
            // 플레이어에게 보여지는 아이템을 설정해줍니다.
            stone.item = ItemStack(Material.COBBLESTONE)

            // 플레이어의 움직임 벡터와 조약돌의 발사 벡터를 분리합니다.
            val playerMovement = player.velocity
            val stoneDirection = location.direction.multiply(1.0)
            stone.velocity = stoneDirection.subtract(playerMovement)

            player.setCooldown(Material.COBBLESTONE, 5)

            // 조약돌 다 썼을 때
            if (player.gameMode != GameMode.CREATIVE) {
                eventItem.amount = eventItem.amount - 1
                player.inventory.setItemInMainHand(eventItem)
                if (eventItem.amount == 0) {
                    for (i in 0..35) {
                        if (player.inventory.getItem(i) != null && player.inventory.getItem(i)!!.type == Material.COBBLESTONE) {
                            val item = player.inventory.getItem(i)!!

                            player.inventory.setItem(i, ItemStack(Material.AIR))
                            player.inventory.setItemInMainHand(item)
                            break
                        }
                    }
                }
            }


            // 고압축 조약돌
        } else if (eventItem.type == Material.FIREWORK_STAR) {
            if (player.getCooldown(Material.FIREWORK_STAR) != 0) return

            // 사운드를 설정합니다.
            world.playSound(location, Sound.ENTITY_ARROW_SHOOT, 0.5f, 0.5f)

            val compact_stone = world.spawn(location, Snowball::class.java)
            compact_stone.shooter = player
            // 플레이어에게 보여지는 아이템을 설정해줍니다.
            compact_stone.item = ItemStack(Material.FIREWORK_STAR)

            // 플레이어의 움직임 벡터와 돌의 발사 벡터를 분리
            // 속도를 조절해줍니다.
            val playerMovement = player.velocity
            val stoneDirection = location.direction.multiply(2.0)
            compact_stone.velocity = stoneDirection.subtract(playerMovement)

            player.setCooldown(Material.FIREWORK_STAR, 5)

            // 파티클 생성
            object : BukkitRunnable() {
                override fun run() {
                    if (compact_stone.isDead || !compact_stone.isValid) {
                        this.cancel()
                        return
                    }
                    world.spawnParticle(Particle.CRIT, compact_stone.location, 10, 0.1, 0.1, 0.1, 0.05)
                }
            }.runTaskTimer(JavaPlugin.getPlugin(StoneBattle::class.java), 0, 1)

            // 고압축 조약돌 다 썼을 때
            if (player.gameMode != GameMode.CREATIVE) {
                eventItem.amount = eventItem.amount - 1
                player.inventory.setItemInMainHand(eventItem)
                if (eventItem.amount == 0) {
                    for (i in 0..35) {
                        if (player.inventory.getItem(i) != null && player.inventory.getItem(i)!!.type == Material.FIREWORK_STAR) {
                            val item = player.inventory.getItem(i)!!

                            player.inventory.setItem(i, ItemStack(Material.AIR))
                            player.inventory.setItemInMainHand(item)
                            break
                        }
                    }
                }
            }


            // 초고압축 압축 조약돌
        } else if (eventItem.type == Material.FIRE_CHARGE) {
            if (player.getCooldown(Material.FIRE_CHARGE) != 0) return

            world.playSound(location, Sound.ENTITY_ENDER_DRAGON_SHOOT, 0.5f, 0.5f)


            val high_compact_stone = world.spawn(location, Fireball::class.java)
            high_compact_stone.shooter = player
            high_compact_stone.direction = location.direction


            // 플레이어의 움직임 벡터와 돌의 발사 벡터를 분리
            val stoneDirection = location.direction.multiply(2.5)  // 빠른 속도로 설정
            high_compact_stone.velocity = stoneDirection

            player.setCooldown(Material.FIRE_CHARGE, 5)

            // 파티클 생성
            object : BukkitRunnable() {
                override fun run() {
                    // 만약 초고압축 조약돌이 지상에 닿았거나, 없어졌거나, 더 이상 월드에 없다면 태스크를 중단합니다.
                    if (high_compact_stone.isDead || !high_compact_stone.isValid) {
                        this.cancel()
                        return
                    }
                    world.spawnParticle(Particle.FLAME, high_compact_stone.location, 10, 0.1, 0.1, 0.1, 0.1)
                }
            }.runTaskTimer(JavaPlugin.getPlugin(StoneBattle::class.java), 0, 1)

            // 초고압축 돌 다 썼을 때
            if (player.gameMode != GameMode.CREATIVE) {
                eventItem.amount = eventItem.amount - 1
                player.inventory.setItemInMainHand(eventItem)
                if (eventItem.amount == 0) {
                    for (i in 0..35) {
                        if (player.inventory.getItem(i) != null && player.inventory.getItem(i)!!.type == Material.FIRE_CHARGE) {
                            val item = player.inventory.getItem(i)!!

                            player.inventory.setItem(i, ItemStack(Material.AIR))
                            player.inventory.setItemInMainHand(item)
                            break
                        }
                    }
                }
            }


            // 플라즈마 압축 조약돌
        } else if (eventItem.type == Material.MAGMA_CREAM) {
            if (player.getCooldown(Material.MAGMA_CREAM) != 0) return

            // 사운드를 설정합니다.
            world.playSound(location, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 0.5f, 0.5f)

            val plasma_stone = world.spawn(location, Snowball::class.java)
            plasma_stone.shooter = player
            // 플레이어에게 보여지는 아이템을 설정해줍니다.
            plasma_stone.item = ItemStack(Material.MAGMA_CREAM)

            // 플레이어의 움직임 벡터와 돌의 발사 벡터를 분리
            // 속도를 조절해줍니다.
            val playerMovement = player.velocity
            val stoneDirection = location.direction.multiply(5.0)
            plasma_stone.velocity = stoneDirection.subtract(playerMovement)

            player.setCooldown(Material.FIREWORK_STAR, 5)

            // 파티클 생성
            object : BukkitRunnable() {
                override fun run() {
                    if (plasma_stone.isDead || !plasma_stone.isValid) {
                        for (i in 1..8) {
                            val randomX = 1.5 - Math.random() * 8
                            val randomZ = 1.5 - Math.random() * 8
                            val lightningLocation = plasma_stone.location.clone().add(randomX, 0.0, randomZ)
                            plasma_stone.world.strikeLightning(lightningLocation)
                        }
                        this.cancel()
                        return
                    }
                    world.spawnParticle(Particle.CRIT_MAGIC, plasma_stone.location, 10, 0.1, 0.1, 0.1, 0.05)
                }
            }.runTaskTimer(JavaPlugin.getPlugin(StoneBattle::class.java), 0, 1)

            // 플라즈마 조약돌 다 썼을 때
            if (player.gameMode != GameMode.CREATIVE) {
                eventItem.amount = eventItem.amount - 1
                player.inventory.setItemInMainHand(eventItem)
                if (eventItem.amount == 0) {
                    for (i in 0..35) {
                        if (player.inventory.getItem(i) != null && player.inventory.getItem(i)!!.type == Material.MAGMA_CREAM) {
                            val item = player.inventory.getItem(i)!!

                            player.inventory.setItem(i, ItemStack(Material.AIR))
                            player.inventory.setItemInMainHand(item)
                            break
                        }
                    }
                }
            }
        }
    }

    // 오브젝트에 맞았을 때의 이벤트를 관리합니다.
    @EventHandler
    fun damageStone(event: ProjectileHitEvent) {
        if (event.hitEntity == null) return

        if (event.entity !is Snowball && event.entity !is Fireball) return

        val projectile = event.entity
        val shooter = projectile.shooter

        // 발사체가 Snowball 또는 Fireball이 아니거나, 발사자가 Player 가 아니거나, 대상이 LivingEntity 가 아닌 경우 반환합니다.
        if ((projectile !is Snowball && projectile !is Fireball) || shooter !is Player || event.hitEntity !is LivingEntity) return


        // if (projectile is Snowball && projectile.item.type !in setOf(Material.COBBLESTONE, Material.FIREWORK_STAR, Material.FIRE_CHARGE, Material.MAGMA_CREAM)) return


        val entity: LivingEntity = event.hitEntity as LivingEntity
        val world = projectile.world
        val location = projectile.location

        entity.noDamageTicks = 0

        // Issue ;
        // 발사한 플레이어의 주된 손 아이템을 확인
        val shooterMainItem = shooter.inventory.itemInMainHand.type

        if (shooterMainItem == Material.COBBLESTONE)
            entity.damage(2.0, shooter)
        else if (shooterMainItem == Material.FIREWORK_STAR)
            entity.damage(4.0, shooter)
        else if (shooterMainItem == Material.FIRE_CHARGE)
            entity.damage(8.0, shooter)
        else if (shooterMainItem == Material.MAGMA_CREAM)
            entity.damage(16.0, shooter)




        world.spawnParticle(
            Particle.BLOCK_CRACK,
            location,
            20,
            0.0,
            0.0,
            0.0,
            0.0,
            Material.COBBLESTONE.createBlockData(),
            true
        )
        world.playSound(location, Sound.BLOCK_STONE_BREAK, 1f, 1f)
    }


    //돌을 방패로 막았을 때
    @EventHandler
    fun onPlayerBlockWithShield(event: EntityDamageByEntityEvent) {
        // 피해를 입은 엔터티가 플레이어인지 확인
        if (event.entity !is Player) return

        val player = event.entity as Player

        if (event.damager !is Projectile) return
        val projectile = event.damager as Projectile

        // 발사체의 발사자가 플레이어인지 확인
        if (projectile.shooter !is Player) return

        val shooter = projectile.shooter as Player

        // 플레이어가 방패로 피해를 막았는지 확인
        if (player.isBlocking) {
            val damageAmount = when {
                projectile is Snowball && projectile.item.type == Material.COBBLESTONE -> 0.6
                projectile is Snowball && projectile.item.type == Material.FIREWORK_STAR -> 1.2
                else -> 0.0
            }

            // 발사한 사람에게 데미지를 줍니다.
            shooter.damage(damageAmount)
        }
    }


    // 블럭에 맞았을 때의 이벤트를 관리합니다.
    @EventHandler
    fun ingroundStone(event: ProjectileHitEvent) {
        if (event.hitBlock == null) return

        val projectile = event.entity
        val projectileItem = when (projectile) {
            is Snowball -> projectile.item.type
            is Fireball -> Material.FIRE_CHARGE
            else -> return
        }

        if (projectileItem !in setOf(
                Material.COBBLESTONE,
                Material.FIREWORK_STAR,
                Material.FIRE_CHARGE,
                Material.MAGMA_CREAM
            )
        ) return
        if (projectile.shooter !is Player) return
        if (event.hitBlock !is Block) return

        val block: Block = event.hitBlock!!
        val world = projectile.world
        val location = projectile.location


        world.spawnParticle(
            Particle.BLOCK_CRACK,
            location,
            20,
            0.0,
            0.0,
            0.0,
            0.0,
            Material.COBBLESTONE.createBlockData(),
            true
        )
        world.playSound(location, Sound.BLOCK_STONE_BREAK, 1f, 1f)

        if (block.type !in setOf(
                Material.COBBLESTONE,
                Material.COBBLESTONE_SLAB,
                Material.COBBLESTONE_STAIRS,
                Material.DIRT
            )
        ) return

        val probability = when (projectileItem) {
            Material.COBBLESTONE -> 20
            Material.FIREWORK_STAR -> 40
            Material.FIRE_CHARGE -> 60
            Material.MAGMA_CREAM -> 100
            else -> 0
        }

        val random = (Math.random() * 100 + 1).toInt()
        if (random < probability) {
            block.breakNaturally(ItemStack(Material.NETHERITE_PICKAXE))
        }
    }


    // 코어에 맞았을 때의 이벤트를 관리합니다.
    fun endGame(message: String) {
        for (players in Bukkit.getOnlinePlayers()) {
            players.sendTitle("게임 종료!", message, 10, 100, 10)
            players.teleport(Location(Bukkit.getWorld("world"), 75.7, 77.0, 77.4, 90.0f, 0.0f))
            players.gameMode = GameMode.CREATIVE
            Bukkit.getScheduler().cancelTasks(JavaPlugin.getPlugin(StoneBattle::class.java))

            red_bar.removePlayer(players)
            blue_bar.removePlayer(players)
            redTeam.clear()
            blueTeam.clear()
        }
    }

    fun handleCoreAttack(
        block: Block,
        team: MutableList<Player>,
        opposingTeam: MutableList<Player>,
        bar: BossBar,
        coreDamage: Double,
        shooter: Player,
        coreMaterial: Material,
        nexusMaterial: Material,
        teamColor: ChatColor
    ) {
        if (!opposingTeam.contains(shooter)) return


        if (block.type == coreMaterial && bar.progress <= coreDamage) {
            block.type = nexusMaterial
            bar.progress = 0.7

            for (players in Bukkit.getOnlinePlayers()) {
                players.sendTitle(
                    "${teamColor}${teamColor.name.capitalize()}${teamColor} Team${ChatColor.WHITE} 억제기 파괴!",
                    "이제 조약돌이 생성됩니다.",
                    10,
                    70,
                    20
                )

                // "${teamColor}${teamColor.name.capitalize()}${teamColor} Team${ChatColor.WHITE} 에서 조약돌이 생성됩니다."

                // 염소 뿔: 소집 소리를 재생합니다.
                for (player in Bukkit.getOnlinePlayers()) {
                    player.performCommand("playsound item.goat_horn.sound.5 master ${player.name} ~ ~ ~ 200.0 1.0")
                }
            }

            startSpawningCobblestoneForTeam(teamColor)
            return
        }

        if (block.type == nexusMaterial && bar.progress <= coreDamage) {
            val winningTeamColor = if (teamColor == ChatColor.RED) ChatColor.BLUE else ChatColor.RED
            endGame(
                "$winningTeamColor${
                    winningTeamColor.name.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    }
                } Team 승리!"
            )
            return
        }

        bar.progress -= coreDamage
        println("Bar Progress: ${bar.progress}")
        shooter.playSound(shooter.location, Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 1f)
        bar.addPlayer(shooter)

        Bukkit.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getPlugin(StoneBattle::class.java), {
            if (bar.progress < 1.0) {
                for (player in opposingTeam) {
                    bar.addPlayer(player)
                }
            } else {
                for (player in opposingTeam) {
                    bar.removePlayer(player)
                }
            }
        }, 100)
        for (player in team) {
            player.sendActionBar(Component.text("${teamColor}코어가 공격받고있습니다!"))
        }
    }

    @EventHandler
    fun attackCoreStone(event: ProjectileHitEvent) {
        if (event.hitBlock == null) return

        val projectile = event.entity
        val projectileItem = when (projectile) {
            is Snowball -> projectile.item.type
            is Fireball -> Material.FIRE_CHARGE
            else -> return
        }

        if (projectileItem !in setOf(
                Material.COBBLESTONE,
                Material.FIREWORK_STAR,
                Material.FIRE_CHARGE,
                Material.MAGMA_CREAM
            )
        ) return
        if (projectile.shooter !is Player) return

        val block: Block = event.hitBlock!!
        val world = projectile.world
        val location = projectile.location
        val shooter: Player = projectile.shooter as Player

        // 코어에 들어가는 직접적인 데미지를 관리합니다.
        val coreDamage: Double = when (projectileItem) {
            Material.COBBLESTONE -> 0.0025
            Material.FIREWORK_STAR -> 0.01
            Material.FIRE_CHARGE -> 0.1
            Material.MAGMA_CREAM -> 0.5
            else -> return
        }

        if (block.location == red_core) {
            handleCoreAttack(
                block,
                redTeam,
                blueTeam,
                red_bar,
                coreDamage,
                shooter,
                Material.RED_CONCRETE,
                Material.RED_GLAZED_TERRACOTTA,
                ChatColor.RED
            )
        } else if (block.location == blue_core) {
            handleCoreAttack(
                block,
                blueTeam,
                redTeam,
                blue_bar,
                coreDamage,
                shooter,
                Material.BLUE_CONCRETE,
                Material.BLUE_GLAZED_TERRACOTTA,
                ChatColor.BLUE
            )
        }
    }

    // 억제기가 파괴되면 돌 생성 이벤트를 관리합니다.
    var redSpawnTaskId: Int? = null
    var blueSpawnTaskId: Int? = null
    val world: World? = Bukkit.getWorld("world")

    val redSpawnLocation: Location = Location(world, 82.0, 77.0, 189.0)
    val blueSpawnLocation: Location = Location(world, 72.0, 77.0, -35.0)

    fun startSpawningCobblestoneForTeam(teamColor: ChatColor) {
        val plugin = JavaPlugin.getPlugin(StoneBattle::class.java)
        when(teamColor) {
            ChatColor.RED -> {
                redSpawnTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, Runnable {
                    val itemCount = Random.nextInt(1, 6) // 1 ~ 5 개의 아이템
                    for (i in 1..itemCount) {
                        world!!.dropItemNaturally(redSpawnLocation, ItemStack(Material.COBBLESTONE))
                    }
                }, 0L, 40L) // (1초 = 20 ticks)
            }
            ChatColor.BLUE -> {
                blueSpawnTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, Runnable {
                    val itemCount = Random.nextInt(1, 6) // 1 ~ 5 개의 아이템
                    for (i in 1..itemCount) {
                        world!!.dropItemNaturally(blueSpawnLocation, ItemStack(Material.COBBLESTONE))
                    }
                }, 0L, 40L)
            }
            else -> {} // 다른 팀 색상이 추가될 경우
        }
    }

    fun stopSpawningCobblestoneForTeam(teamColor: ChatColor) {
        when(teamColor) {
            ChatColor.RED -> {
                if (redSpawnTaskId != null) {
                    Bukkit.getScheduler().cancelTask(redSpawnTaskId!!)
                    redSpawnTaskId = null
                }
            }
            ChatColor.BLUE -> {
                if (blueSpawnTaskId != null) {
                    Bukkit.getScheduler().cancelTask(blueSpawnTaskId!!)
                    blueSpawnTaskId = null
                }
            }
            else -> {} // 다른 팀 색상이 추가될 경우
        }
    }

    // 상대 팀의 상자를 열 수 없게 하는 이벤트를 관리합니다.
    @EventHandler
    fun handleTeamChestInteract(event: PlayerInteractEvent) {
        val player = event.player
        val block = event.clickedBlock

        if (block == null || block.type != Material.CHEST) {
            return
        }

        if (isInRedTeamTerritory(block) && blueTeam.contains(player)) {
            player.sendMessage("[!] ${ChatColor.RED}Red Team ${ChatColor.WHITE}의 상자는 열 수 없습니다!")
            event.isCancelled = true
            return
        }

        if (isInBlueTeamTerritory(block) && redTeam.contains(player)) {
            player.sendMessage("[!] ${ChatColor.BLUE}Blue Team ${ChatColor.WHITE}의 상자는 열 수 없습니다!")
            event.isCancelled = true
        }
    }

    fun isInRedTeamTerritory(block: Block): Boolean {
        return (block.x in 70..84 && block.y in 76..84 && block.z == 170);
    }

    fun isInBlueTeamTerritory(block: Block): Boolean {
        return (block.x in 70..84 && block.y in 76..84 && block.z == -16);
    }

}
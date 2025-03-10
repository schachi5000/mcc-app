package net.schacher.mcc.shared.localization

import androidx.compose.runtime.Composable
import marvelchampionscompanion.shared.generated.resources.Res
import marvelchampionscompanion.shared.generated.resources.aggression
import marvelchampionscompanion.shared.generated.resources.ally
import marvelchampionscompanion.shared.generated.resources.alter_ego
import marvelchampionscompanion.shared.generated.resources.attachment
import marvelchampionscompanion.shared.generated.resources.environment
import marvelchampionscompanion.shared.generated.resources.event
import marvelchampionscompanion.shared.generated.resources.hero
import marvelchampionscompanion.shared.generated.resources.justice
import marvelchampionscompanion.shared.generated.resources.leadership
import marvelchampionscompanion.shared.generated.resources.main_scheme
import marvelchampionscompanion.shared.generated.resources.minion
import marvelchampionscompanion.shared.generated.resources.obligation
import marvelchampionscompanion.shared.generated.resources.player_side_scheme
import marvelchampionscompanion.shared.generated.resources.protection
import marvelchampionscompanion.shared.generated.resources.resource
import marvelchampionscompanion.shared.generated.resources.side_scheme
import marvelchampionscompanion.shared.generated.resources.support
import marvelchampionscompanion.shared.generated.resources.treachery
import marvelchampionscompanion.shared.generated.resources.upgrade
import marvelchampionscompanion.shared.generated.resources.villain
import net.schacher.mcc.shared.model.Aspect
import net.schacher.mcc.shared.model.CardType
import org.jetbrains.compose.resources.stringResource

val CardType.label: String
    @Composable
    get() = when (this) {
        CardType.HERO -> stringResource(Res.string.hero)
        CardType.ALTER_EGO -> stringResource(Res.string.alter_ego)
        CardType.ALLY -> stringResource(Res.string.ally)
        CardType.EVENT -> stringResource(Res.string.event)
        CardType.SUPPORT -> stringResource(Res.string.support)
        CardType.UPGRADE -> stringResource(Res.string.upgrade)
        CardType.RESOURCE -> stringResource(Res.string.resource)
        CardType.VILLAIN -> stringResource(Res.string.villain)
        CardType.MAIN_SCHEME -> stringResource(Res.string.main_scheme)
        CardType.SIDE_SCHEME -> stringResource(Res.string.side_scheme)
        CardType.PLAYER_SIDE_SCHEME -> stringResource(Res.string.player_side_scheme)
        CardType.ATTACHMENT -> stringResource(Res.string.attachment)
        CardType.MINION -> stringResource(Res.string.minion)
        CardType.TREACHERY -> stringResource(Res.string.treachery)
        CardType.ENVIRONMENT -> stringResource(Res.string.environment)
        CardType.OBLIGATION -> stringResource(Res.string.obligation)
    }

val Aspect.label: String
    @Composable
    get() = when (this) {
        Aspect.AGGRESSION -> stringResource(Res.string.aggression)
        Aspect.PROTECTION -> stringResource(Res.string.protection)
        Aspect.JUSTICE -> stringResource(Res.string.justice)
        Aspect.LEADERSHIP -> stringResource(Res.string.leadership)
    }
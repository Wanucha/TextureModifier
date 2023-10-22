package cz.wa.texturemodifier.command

import cz.wa.texturemodifier.SettingsOld
import java.awt.Color

abstract class AbstractCommand(val settings: SettingsOld) : Command {
    protected fun getAlpha(rgba: Int): Int {
        return Color(rgba, true).alpha
    }
}

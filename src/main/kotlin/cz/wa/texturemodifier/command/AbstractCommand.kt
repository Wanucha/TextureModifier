package cz.wa.texturemodifier.command

import cz.wa.tilepalleteresize.Settings
import java.awt.Color

abstract class AbstractCommand(val settings: Settings) : Command {
    protected fun getAlpha(rgba: Int): Int {
        return Color(rgba, true).alpha
    }
}
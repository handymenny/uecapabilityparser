package it.smartphonecombo.uecapabilityparser.bean

interface IComponent {
    fun compareTo(iComponent: IComponent): Int
    fun toStringExtended(): String

    var band: Int
    var classDL: Char
    var classUL: Char
    var mimoDL: Int
    var mimoUL: Int
    var modDL: String?
    var modUL: String?

    companion object {
        val defaultComparator: Comparator<IComponent> by
        lazy(LazyThreadSafetyMode.PUBLICATION) {
            Comparator.comparing { obj: IComponent -> obj.band }
                .thenComparing { obj: IComponent -> obj.classDL }
                .thenComparing { obj: IComponent -> obj.mimoDL }
                .thenComparing { obj: IComponent -> obj.classUL }
                .thenComparing { obj: IComponent -> obj.mimoUL }
        }
    }
}
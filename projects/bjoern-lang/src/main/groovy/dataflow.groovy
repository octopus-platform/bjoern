reachesWith = { args ->
    _().outE("REACHES").filter { it.aloc.name in args }.inV()
}

reachesWithout = { args ->
    _().outE("REACHES").filter { !(it.aloc.name in args) }.inV()
}

revReachesWith = { args ->
    _().inE("REACHES").filter { it.aloc.name in args }.outV()
}

revReachesWithout = { args ->
    _().inE("REACHES").filter { !(it.aloc.name in args) }.outV()
}

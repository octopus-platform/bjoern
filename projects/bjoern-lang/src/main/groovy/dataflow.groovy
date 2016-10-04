reachesWith = { args ->
    _().outE("REACHES").filter { it.aloc in args }.inV()
}

reachesWithout = { args ->
    _().outE("REACHES").filter { !(it.aloc in args) }.inV()
}

revReachesWith = { args ->
    _().inE("REACHES").filter { it.aloc in args }.outV()
}

revReachesWithout = { args ->
    _().inE("REACHES").filter { !(it.aloc in args) }.outV()
}

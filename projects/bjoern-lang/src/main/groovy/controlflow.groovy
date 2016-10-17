package bjoernsteps;

getPrevInstr = { def pattern ->
  _().in('NEXT_INSTR').dedup()
 .loop(2){it.path.isSimple() && !it.object.repr.contains(pattern) }         
 {it.object.repr.contains(pattern)}
 .enablePath()
}

getNextInstr = { def pattern ->
  _().out('NEXT_INSTR').dedup()
 .loop(2){it.path.isSimple() && !it.object.repr.contains(pattern) }         
 {it.object.repr.contains(pattern)}
 .enablePath()
}

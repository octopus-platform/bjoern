package bjoernsteps;

getPrevInstr = { def args ->
  def pattern = args[0]
  _().in('NEXT_INSTR').dedup()
 .loop(2){it.path.isSimple() && !it.object.repr.contains(pattern) }         
 {it.object.repr.contains(pattern)}
 .enablePath()
}

getNextInstr = { def args ->
  def pattern = args[0]
  _().out('NEXT_INSTR').dedup()
 .loop(2){it.path.isSimple() && !it.object.repr.contains(pattern) }         
 {it.object.repr.contains(pattern)}
 .enablePath()
}

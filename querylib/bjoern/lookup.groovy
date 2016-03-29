
getCallsTo = { callee ->
 query = String.format('nodeType:Instr AND repr:call*')
 queryNodeIndex(query).filter{ it.repr.contains(callee) }
}

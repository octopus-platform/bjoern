
inSameBB = { args ->
  _().in('IS_BB_OF').out('IS_BB_OF')
}

funcToInstr = {
  _().in('INTERPRETABLE_AS').out('INTERPRETABLE_AS')
  .filter{ it.nodeType == 'Instr'}
}

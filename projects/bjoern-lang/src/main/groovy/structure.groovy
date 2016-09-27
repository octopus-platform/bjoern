package bjoernsteps;

inSameBlock = {
    _().in('IS_BB_OF').out('IS_BB_OF')
}

funcToFirstInstr = {
    _().in('INTERPRETABLE_AS').out('INTERPRETABLE_AS')
            .filter { it.nodeType == 'Instr' }
}

funcToAlocs = {
    _().out("ALOC_USE_EDGE")
}

instrToBlock = {
    _().in("IS_BB_OF")
}

blockToFunc = {
    _().in("IS_FUNC_OF")
}

instrToFunc = {
    _().instrToBlock().blockToFunc()
}

followCallToFunc = {
    _().out("CALL").instrToFunc()
}
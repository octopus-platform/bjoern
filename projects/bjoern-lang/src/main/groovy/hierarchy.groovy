package bjoernsteps;

inSameBB = {
    _().in('IS_BB_OF').out('IS_BB_OF')
}

funcToFirstInstr = {
    _().in('INTERPRETABLE_AS').out('INTERPRETABLE_AS')
            .filter{ it.nodeType == 'Instr' }
}

instrToBB = {
    _().in("IS_BB_OF")
}

bBToFunc = {
    _().in("IS_FUNC_OF")
}

instrToFunc = {
    _().instrToBB().bBToFunc()
}

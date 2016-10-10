package bjoernsteps;

queryNodeIndex = { luceneQuery ->

    queryStr = 'SELECT * FROM V WHERE [addr,code,comment,esil,key,nodeType,repr] LUCENE "' + luceneQuery + '"';
    query = new com.orientechnologies.orient.core.sql.OCommandSQL(queryStr);
    g.getRawGraph().command(query).execute().toList()._().transform { g.v(it.getIdentity()) }
}

getCallsTo = { callee ->
    queryNodeIndex("nodeType:Flag").filter {
        it.code.contains(callee)
    }.copySplit(_().in("IS_ANNOTATED_BY").out("INTERPRETABLE_AS").has("nodeType", "Instr").in("CALL"),
    _().transform{queryNodeIndex("nodeType:Instr AND addr:" + Integer.toHexString(Integer.parseInt(it.addr,16) - 1))}
            .scatter()).exhaustMerge().dedup()
}

getFunctions = { name ->
    queryNodeIndex('nodeType:Func')
            .filter { it.repr.contains(name) }
}

jumpToAddr = { addr ->
    if (addr instanceof String) {
        addr = addr.startsWith("0x") ? addr.substring(2) : addr
        addr = Long.parseLong(addr, 16)
    }
    addr = Long.toHexString(addr)
    query = String.format('nodeType:Root AND addr:%s', addr)
    queryNodeIndex(query)
}

jumpToInstrAtAddr = { addr ->
    jumpToAddr(addr).out("INTERPRETABLE_AS").filter { it.nodeType == "Instr" }
}

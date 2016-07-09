package bjoernsteps;

queryNodeIndex = { luceneQuery ->

    queryStr = 'SELECT * FROM V WHERE [addr,childNum,code,comment,esil,key,nodeType,repr] LUCENE "' + luceneQuery + '"';
    query = new com.orientechnologies.orient.core.sql.OCommandSQL(queryStr);
    g.getRawGraph().command(query).execute().toList()._().transform { g.v(it.getIdentity()) }
}

getCallsTo = { callee ->
    query = String.format('nodeType:Instr AND repr:call*')
    queryNodeIndex(query).filter { it.repr.contains(callee) }
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

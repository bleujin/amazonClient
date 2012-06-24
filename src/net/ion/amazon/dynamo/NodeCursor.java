package net.ion.amazon.dynamo;

import java.util.List;

import net.ion.framework.db.Page;
import net.ion.framework.util.ListUtil;

public class NodeCursor
{
	List<Node> nodes;
	Page page;
	int index;
	
	public NodeCursor(List<Node> nodes, Page page)
	{
		this.nodes = nodes;
		this.page = page;
	}

	public List<Node> toList()
	{
		int listnum = page.getListNum();
		int pageno = page.getPageNo();
		List<Node> res = ListUtil.newList();
		
		for (int i = listnum * (pageno - 1); i < nodes.size() && i < listnum * pageno; i ++)
		{
			res.add(nodes.get(i));
		}
		
		return res;
	}
}

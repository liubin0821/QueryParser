<?php
/******************************************************************************
 *  文件名： XMLTools.php
 *  创建者： 夏炜
 *  创建时间： 2012-03-23
 *  内容描述： 一些xml工具
 *******************************************************************************/

class Web_Common_XMLTools{

	/******************************************************************************
	 * 函数： XMLToArray 
	 * 创建者： 夏炜
	 * 创建时间： 2012-03-23
	 * 函数描述： 将XML字符串转换成数组，其中text部分转换成为_value
	 *******************************************************************************/
	public static function XMLToArray($xml, $compat = false)
	{
		$dom = new DOMDocument();
		$dom->preserveWhiteSpace = false;
		$dom->loadXML($xml);
		return Web_Common_XMLTools::DomToArray($dom, $compat);
	}

	/******************************************************************************
	 * 函数： XMLFileToArray 
	 * 创建者： 夏炜
	 * 创建时间： 2012-03-23
	 * 函数描述： 将XML文件转换成数组，其中text部分转换成为_value
	 *******************************************************************************/
	public static function XMLFileToArray($xml, $compat = false)
	{
		$dom = new DOMDocument();
		$dom->preserveWhiteSpace = false;
		$dom->load($xml);
		return Web_Common_XMLTools::DomToArray($dom, $compat);
	}

	/******************************************************************************
	 * 函数： DomToArray 
	 * 创建者： 夏炜
	 * 创建时间： 2012-03-23
	 * 函数描述： 将DOMTree转换成数组，其中text部分转换成为_value
	 *******************************************************************************/
	public static function DomToArray($root, $compat = false)
	{
		$result = array();

		if($root->hasAttributes())
		{
			$attrs = $root->attributes;
			foreach($attrs as $i => $attr)
			{
				$result["_attr_".$attr->name] = $attr->value;
			}
		}

		$children = $root->childNodes;


		$childtype = array();
		for($i=0; $i<$children->length; $i++)
		{
			$child = $children->item($i);
			if($child->nodeType == XML_TEXT_NODE)
			{
				if(isset($result['_value']))
				{
					$result['_value'] .= $child->nodeValue;
				}
				else
				{
					$result['_value'] = $child->nodeValue;
				}
			}
			else
			{
				if(!isset($result[$child->nodeName]))
				{
					$childtype[$child->nodeName] = 1;
					$result[$child->nodeName] = Web_Common_XMLTools::DomToArray($child, $compat);
				}
				else
				{
					if($childtype[$child->nodeName] == 1)
					{
						$tmp = $result[$child->nodeName];
						$result[$child->nodeName] = array($tmp);
						$childtype[$child->nodeName] = 2;
						$result['__xml_array__items__'][$child->nodeName] = 1;
					}
					$result[$child->nodeName][] = Web_Common_XMLTools::DomToArray($child, $compat);
				}
			}
		}
		if($compat === true)
		{
			if(count($result) == 1 && isset($result['_value']))
				$result = $result['_value'];
			if(count($result) == 0)
				$result = "";
		}
		return $result;
	}

	/******************************************************************************
	 * @brief 把上面组成的xmlnode中的一个节点以数组形式返回
	 *  
	 *    
	 * @param parentnode 上一级节点
	 * @param parentnode 下一级节点的名称
	 * 
	 * @return 数组
	 *******************************************************************************/
	public static function getXMLTreeArray($parentnode, $childname)
	{
		if(!isset($parentnode[$childname]))
			return array();
		if(!isset($parentnode['__xml_array__items__']))
			return array($parentnode[$childname]);
		if(!isset($parentnode['__xml_array__items__'][$childname]))
			return array($parentnode[$childname]);
		return $parentnode[$childname];
	}
}

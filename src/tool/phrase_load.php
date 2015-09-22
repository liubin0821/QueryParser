<?php
include("XMLTools.php");
dbInit();
dbClean();

/*
    $fn = "../../data/stock/stock_onto.xml";
    $res = Web_Common_XMLTools::XMLFileToArray($fn, true);
    foreach($res['node']['class'] as $id=>$v)
    {
        $ov = $v;
        unset($v['_attr_label']);
        unset($v['_attr_report_type']);
        unset($v['prop']);
        unset($v['alias']);
        unset($v['description']);
        unset($v['__xml_array__items__']);
        if(count($v) == 0)
            continue;
        print_r($v);
        print_r($ov);
        sleep(10);
        continue;
        die();
    }
    die();
    print_r($res);
 */
    $fn = "../../data/stock/stock_phrase_semantic.xml";
    $res = Web_Common_XMLTools::XMLFileToArray($fn, true);
    //print_r($res);
    foreach($res['Config']['KeywordGroup'] as $id=>$v)
    {
        echo "=== $id ===\n";
        echo $v['_attr_id']."\n";
        //dbInsert($v, "keywordgroup");
        continue;
    }
    foreach($res['Config']['IndexGroup'] as $id=>$v)
    {
        echo "=== $id ===\n";
        echo $v['_attr_id']."\n";
        //dbInsert($v, "indexgroup");
        continue;
        print_r($v);
    }
    $synids = new idmap();
    $semids = new idmap();
    foreach($res['Config']['SyntacticPattern'] as $id=>$v)
    {
        echo "=== $id ===\n";
        $v['_attr_id'] = $synids->get_id($v['_attr_id']);
        $v['SemanticBind']['_attr_BindTo'] = $semids->get_id($v['SemanticBind']['_attr_BindTo']);
        $v['SyntacticElement'] = Web_Common_XMLTools::getXMLTreeArray($v, "SyntacticElement");
        echo $v['_attr_id']."\t ==> ".$v['SemanticBind']['_attr_BindTo']."\n";
        $v = transTables($v);
        //dbInsert($v, "syntactic");
        continue;
    }
    foreach($res['Config']['SemanticPattern'] as $id=>$v)
    {
        echo "=== $id ===\n";
        $v['_attr_id'] = $semids->get_id($v['_attr_id']);
        echo $v['_attr_id']."\n";
        $v['Argument'] = Web_Common_XMLTools::getXMLTreeArray($v, "Argument");
        $v['InternalRepresentation']['Operation']['Operand'] = Web_Common_XMLTools::getXMLTreeArray($v['InternalRepresentation']['Operation'], "Operand");
        transTables($v);
        dbInsert($v, "semantic");
        continue;
        die();
    }
	file_get_contents("http://192.168.201.164:9100/parser/?reload=1&question=300001");

    function dbInit()
    {
        mysql_connect("172.20.0.52", "xiawei", "xiawei123456");
        mysql_select_db("phrase");
        mysql_query("set names utf8;");
    }
    function dbClean()
    {
        #mysql_query("truncate table `indexgroup`")or die(mysql_error());
        #mysql_query("truncate table `keywordgroup`");
        mysql_query("truncate table `semantic`");
        #mysql_query("truncate table `syntactic`");
    }
    function dbInsert($data, $table)
    {
        if($table == 'keywordgroup')
        {
            $sql = "insert into `keywordgroup`(`id`,`description`,`keywords`) values('".$data['_attr_id']."', '"
                .mysql_escape_string($data['Description'])."', '"
                .mysql_escape_string(json_encode(Web_Common_XMLTools::getXMLTreeArray($data, "Keyword")))."')";
        }
        else if($table == 'indexgroup')
        {
            $sql = "insert into `indexgroup`(`id`,`description`,`indexs`) values('".$data['_attr_id']."', '"
                .mysql_escape_string($data['Description'])."', '"
                .mysql_escape_string(json_encode($data['Index']))."')";
        }
        else if($table == 'syntactic')
        {
            $syntacticElement = json_encode($data['SyntacticElement']);
            $semanticArgument = isset($data['SemanticBind']['SemanticArgumenmt'])?json_encode($data['SemanticBind']['SemanticArgument']):"[]";
            $semanticArgumentDependency = isset($data['SemanticBind']['SemanticArgumentDependency'])?json_encode($data['SemanticBind']['SemanticArgumentDependency']):"[]";
            $modifier = isset($data['modifiler'])?json_encode($data['moidifiler']):"[]";
            $syntacticElement = json_encode($data['SyntacticElement']);
            $sql = "insert into `syntactic`(`id`,`semanticId`,`syntacticElement`,`semanticArgument`,`semanticArgumentDependency`,`modifier`,`description`) ".
                "values('"
                .$data['id']."','"
                .$data['SemanticBind']['BindTo']."','"
                .mysql_escape_string($syntacticElement)."','"
                .mysql_escape_string($semanticArgument)."','"
                .mysql_escape_string($semanticArgumentDependency)."','"
                .mysql_escape_string($modifier)."','"
                .mysql_escape_string($data['Description'])."')";
            //die("$sql\n");
        }
        else if($table == 'semantic')
        {
            $dependencyArgument = "[]";
            $independencyArgument = "[]";
            $argument = "[]";
			$isKeyValue = "";
            $internalRepresentation = "[]";
            $uiRepresentation = "";
            $chineseRepresentation = "";

            $argument = json_encode($data['Argument']);
            if(isset($data['ArgumenmtDependency']))
            {
                $ad = $data['ArgumentDependency'];
                $dependencyArgument = isset($ad['DependencyArgumenmt'])?json_encode($ad['DependencyArgument']):"[]";
                $independencyArgument = isset($ad['IndependencyArgumenmt'])?json_encode($ad['IndependencyArgument']):"[]";
            }
			if(isset($data['IsKeyValue']))
                $isKeyValue = $data['IsKeyValue'];
            if(isset($data['InternalRepresentation']))
                $internalRepresentation = json_encode($data['InternalRepresentation']);
            if(isset($data['UIRepresentation']))
                $uiRepresentation = $data['UIRepresentation'];
            if(isset($data['ChineseRepresentation']))
                $chineseRepresentation = $data['ChineseRepresentation'];

            $sql = "insert into `semantic`(`id`,`argument`,`argumentDependency`,`argumentIndependency`,`isKeyValue`,`internalRepresentation`, `uiRepresentation`,`chineseRepresentation`,`description`) ".
                "values('"
                .$data['id']."','"
                .mysql_escape_string($argument)."','"
                .mysql_escape_string($argumentDependency)."','"
                .mysql_escape_string($argumentIndependency)."','"
				.mysql_escape_string($isKeyValue)."','"
                .mysql_escape_string($internalRepresentation)."','"
                .mysql_escape_string($uiRepresentation)."','"
                .mysql_escape_string($chineseRepresentation)."','"
                .mysql_escape_string($data['Description'])."')";
            //die("$sql\n");
        }
        else
        {
            echo "not insert\n";
            return false;
        }

        echo "$sql\n";
        if(!mysql_query($sql))
        {
            echo mysql_error();
            echo "\n";
        }
    }

    class idmap
    {
        private $_idmaps = array();
        private $_maxid = 1;

        function __construct()
        {
            ;
        }
        function get_id($oid)
        {
            return $oid;
            if(!isset($this->_idmpas[$oid]))
            {
                $this->_idmpas[$oid] = $this->_maxid;
                $this->_maxid += 1;
            }
            return $this->_idmpas[$oid];
        }
    }
    function transTables(&$table)
    {
        if(!is_array($table))
            return $table;
        foreach($table as $id => &$v)
        {
            if(substr($id, 0, 6) == "_attr_")
            {
                $nid = substr($id, 6);
                $table[$nid] = transTables($v);
                unset($table[$id]);
            }
            else if(substr($id, 0, 6) == "__xml_")
            {
                unset($table[$id]);
            }
            else
            {
                transTables($v);
            }
        }
        return $table;
    }
?>

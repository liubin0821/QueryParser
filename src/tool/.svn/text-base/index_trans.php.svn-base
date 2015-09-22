<?php

$ontofile = "../../data/stock/stock_onto.xml";
$dicttransfile = "../../../ltp-server/dicts/stock_index_trans.dict";
$dictindexfile = "../../../ltp-server/dicts/stock_index.dict";

$trans = load_trans_dicts($dicttransfile);
$indexs = load_indexs($ontofile);
$dicts = load_dicts($dictindexfile);
dicts_comp($trans, $indexs, $dicts);

function dicts_comp($a, $b, $c)
{
    foreach($a as $k => $v)
    {
        if(isset($b[$v]))
            echo "** OK **  ";
        if(isset($c[$v]))
            echo "** OK *** ";
        echo "$k  ==> $v\n";
        
    }
}
function load_indexs($ontofile)
{
    $res = array();
    $f = fopen($ontofile,"r");
    if($f)
    {
        while($l = fgets($f))
        {
            if(preg_match('/<class label="([^"]+)"/', $l, $m))
            {
                $index = $m[1];
                $res[$index] = $index;
            }
        }
    }
    return($res);
}

function load_trans_dicts($dictfile)
{
    $res = array();
    $f = fopen($dictfile,"r");
    if($f)
    {
        while($l = fgets($f))
        {
            if(preg_match('/#([^\/]+)\/\*.*trans:(.*);\$/', $l, $m))
            {
                $index = $m[1];
                $trans = $m[2];
                $res[$index] = $trans;
            }
        }
    }
    return($res);
}

function load_dicts($dictfile)
{
    $res = array();
    $f = fopen($dictfile,"r");
    if($f)
    {
        while($l = fgets($f))
        {
            if(preg_match('/#([^\/]+)\/\*/', $l, $m))
            {
                $index = $m[1];
                $res[$index] = $index;
            }
        }
    }
    return($res);
}
?>

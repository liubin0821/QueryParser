<?php

error_reporting(0);
$filenameList = array();
if(count($argv) > 1)
{
    $filenameList = array_slice($argv, 1);
}
else
{
    $filenameList[] = "../../../ltp-server/dicts/stock_trans.dict";
}

$sum = array();
$collect = array();
$propSum = array();
foreach($filenameList as $filename)
{
    $sum = array();
    $collect = array();
    $rets = array();
    $f = fopen($filename, "r");
    if($f)
    {
        while($l = fgets($f))
        {
            $l = trim($l);
            $ret = dict_parser($l);
            $sum[$ret['valuetype']] += 1;
            $rets[$ret['valuetype']][] = $ret;
            $collect[$ret['valuetype']][] = $l;
        }
    }
    echo "$filename\n";
    print_r($sum);
    foreach($rets['onto_value'] as $v)
    {
        $prop = $v['value'];
        $pos = strpos($prop, "info");
        if($pos !== false)
            $prop = substr($prop, 0, $pos);
        $prop = trim($prop, ";");
        $props = explode("|", substr($prop, 5));
        foreach($props as $p)
        {
            $propSum[$p] += 1;
        }
        //echo $v['txt']."\t".join("|", $props)."\n";
        //print_r($v);die;
    }
}
asort($propSum);
print_r($propSum);
die();
print_r($sum);
die();
print_r($collect['']);
foreach($collect as $k => $v)
{
    $fn = "dict_$k.txt";
    $f = fopen($fn, "w");
    if($f)
    {
        foreach($v as $l)
        {
            fprintf($f, "%s\n", $l);
        }
        fclose($f);
    }
}
function dict_parser($l)
{
    $res = array();
    $l = trim($l);
    /*    
    if(preg_match('/^#(.+)\/\*Cate=([^;]*);Seg=([^;]*);Value=(.*);infos=(.*);+\$$/ims', $l, $m))
    {
        $res['txt'] = $m[1];
        $res['cate'] = $m[2];
        $res['seg'] = $m[3];
        $res['value'] = $m[4];
        $res['infos'] = $m[5];
        echo $m[5]."\n";
        return $res;
        echo "$l\n";
        print_r($m);die;    
    }
    */
    if(preg_match('/^#(.+)\/\*Cate=([^;]*);Seg=([^;]*);Value=(.*);+\$$/ims', $l, $m))
    {
        $res['txt'] = $m[1];
        $res['cate'] = $m[2];
        $res['seg'] = $m[3];
        $val = explode(":", $m[4], 2);
        $res['valuetype'] = $val[0];
        $res['value'] = (isset($val[1]))?$val[1]:0;
        //echo $val[0]."\n";
        return $res;
    }
    else
    {
        echo "$l\n";
        die;
    }
}
?>

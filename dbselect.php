<html>
<head>
<title>PHP TEST</title>
</head>
<body>

<?php

$db_kuru = mysql_connect('localhost', 'kurutest', 'kurutest');
if (!$db_kuru) {
        die('接続失敗です。'.mysql_error());
}

print('<p>接続に成功しました。</p>');

$db_select = mysql_select_db('kuru_test', $db_kuru);
if (!$db_select){
    die('データベース選択失敗です。'.mysql_error());
}

print('<p>Kuru_testデータベースを選択しました。</p>');

mysql_set_charset('utf8');

$select = mysql_query('SELECT ID, Latitude, Longitude, Category, Level FROM ProRSC');
if (!$select) {
    die('クエリーが失敗しました。'.mysql_error());
}

while ($row = mysql_fetch_assoc($select)) {
    print('<p>');
    print('ID='.$row['ID']);
    print(',Latitude='.$row['Latitude']);
    print(',Longiitude='.$row['Longitude']);
    print(',Category='.$row['Category']);
    print(',Level='.$row['Level']);
    print('</p>');
}

$close_flag = mysql_close($db_kuru);

if ($close_flag){
    print('<p>切断に成功しました。</p>');
}

?>
</body>
</html>

let delegators = [];
makeTable()

function getTotalSp(delegators) {
  return new Promise((resolve, reject) => {
    let total = 0;
    for (let i in delegators) {
      total += Number(delegators[i].sp);
    }
    resolve(total);
  });
}

function getDelegators() {
  return new Promise((resolve, reject) => {
    const url = window.location.href + "token/summary";
    axios.get(url).then(function (response) {
      if (response.status == 200) {
        delegators = response.data;
        resolve(delegators);
      }
    });
  });
}

async function makeTable() {
  let delegators = await getDelegators();
  let totalSp = await getTotalSp(delegators);
  let totalToken = 0;
  var s = '<h4><B>' + delegators.length + '</B> Delegator(s) <div id="stats"> </div></h4>';
  s += '<table id="dvlist" class="sortable">';
  s += '<thead><tr><th>NO.</th><th>Steem ID</th><th>SP权重</th><th>代理时间</th><th>总令牌数</th><th>今日令牌数量</th></tr></thead><tbody>';
  for (let i in delegators) {
    totalToken += delegators[i].token;
    s += '<tr>';
    s += '<td>' + delegators[i].ID + '</td>';
    s += '<td><a target=_blank rel=nofollow href="https://steemit.com/@'
        + delegators[i].steem_id + '">@' + delegators[i].steem_id
        + '</a><BR/></td>';
    s += '<td>' + (delegators[i].sp).toFixed(2) + '</td>';
    s += '<td>' + delegators[i].agent_time + '</td>';
    s += '<td>' + (delegators[i].totalToken).toFixed(2) + '</td>';
    s += '<td>' + (delegators[i].token).toFixed(2) + '</td>';

    s += '</tr>';
  }
  s += '</tbody>';
  s += '<tfoot><tr>';
  s += '<th>Total: </th><th></th><th>' + totalSp.toFixed(2)
      + ' SP</th><th></th><th></th><th>' + totalToken.toFixed(2)
      + ' IN</th>';
  s += '</tr></tfoot>';
  s += '</table>';
  $('div#ascii').html(s);
  sorttable.makeSortable(document.getElementById("dvlist"));
  $('div#stats').html(totalSp.toFixed(2) + " SP");

}

function download() {
  let dataCSV = "\ufeffNO.,Steem ID,SP权重,代理时间,总令牌数,今日令牌数量\n";
  let i = 1;

  delegators.forEach(delegator => {
    let line = i + "," + delegator.steem_id + "," + delegator.sp + ","
        + delegator.agent_time + "," + delegator.totalToken + ","
        + delegator.token + "\n";
    dataCSV += line;
    i++;
  });

  let csvTXT = document.createElement('a');
  csvTXT.setAttribute('href',
      'data:text/plain;charset=utf-8,' + encodeURIComponent(dataCSV));

  let d = new Date();
  let descriptor = d.getFullYear().toString() + d.getDate().toString()
      + d.getHours().toString() + d.getSeconds().toString();
  csvTXT.setAttribute('download', descriptor + ".csv");

  csvTXT.style.display = 'none';
  document.body.appendChild(csvTXT);

  csvTXT.click();

  document.body.removeChild(csvTXT);
}
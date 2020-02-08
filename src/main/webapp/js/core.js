let delegators = [];
let cnstmDelegators = [];
let whereInDelegators = [];
const cnstmAccount = "cnstm";
const whereInAccount = "wherein";

createTable(cnstmAccount);
createTable(whereInAccount);

// makeTable();

function getTotalSp(delegators) {
  return new Promise((resolve, reject) => {
    let total = 0;
    for (let i in delegators) {
      total += Number(delegators[i].sp);
    }
    resolve(total);
  });
}

function getDelegators(account) {
  return new Promise((resolve, reject) => {
    const url = window.location.href + "token/summary?account=" + account;
    axios.get(url).then(function (response) {
      if (response.status == 200) {
        delegators = response.data;
        resolve(delegators);
      }
    });
  });
}

async function createTable(account) {
  let delegators = await getDelegators(account);
  if ("cnstm" == account) {
    cnstmDelegators = delegators;
  } else if ("wherein" == account) {
    whereInDelegators = delegators;
  }
  let totalSp = await getTotalSp(delegators);
  let totalToken = 0;
  let totalTokenAllDays = 0;
  const tableId = account + "Table";
  let statsDiv = $('div#' + account).prev();
  var statsDivHtml = statsDiv.html() + '<br /><a><b>' + delegators.length
      + '</b> Delegator(s)</a><br /><a>' + totalSp.toFixed(2) + ' SP</a>';
  statsDiv.html(statsDivHtml);
  var s = '<div><a><b>' + delegators.length + '</b> Delegator(s)</a><br /><a>'
      + totalSp.toFixed(2) + ' SP</a></div>';
  s = '<table id="' + tableId + '" class="sortable">';
  s += '<thead><tr><th>NO.</th><th>Steem ID</th><th>SP权重</th><th>代理时间</th><th>总令牌数</th><th>今日令牌数量</th></tr></thead><tbody>';
  for (let i in delegators) {
    totalToken += delegators[i].token;
    totalTokenAllDays += delegators[i].totalToken;
    let number = Number(i) + 1;
    s += '<tr>';
    s += '<td>' + number + '</td>';
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
      + ' SP</th><th></th><th>' + totalTokenAllDays.toFixed(2)
      + '</th><th>' + totalToken.toFixed(2) + ' IN</th>';
  s += '</tr></tfoot>';
  s += '</table>';
  $('div#' + account).html(s);
  sorttable.makeSortable(document.getElementById(tableId));
}

function download(account) {
  if ("cnstm" == account) {
    delegators = cnstmDelegators;
  } else if ("wherein" == account) {
    delegators = whereInDelegators;
  }
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

  let descriptor = account + dateFormat("YYYYmmddHHMMSS", new Date());
  csvTXT.setAttribute('download', descriptor + ".csv");

  csvTXT.style.display = 'none';
  document.body.appendChild(csvTXT);

  csvTXT.click();

  document.body.removeChild(csvTXT);
}

/**
 * Format date
 * @param fmt format standard.
 * @param date date.
 * @returns {Time formatted string}
 */
function dateFormat(fmt, date) {
  let ret;
  let opt = {
    "Y+": date.getFullYear().toString(),
    "m+": (date.getMonth() + 1).toString(),
    "d+": date.getDate().toString(),
    "H+": date.getHours().toString(),
    "M+": date.getMinutes().toString(),
    "S+": date.getSeconds().toString()
  };
  for (let k in opt) {
    ret = new RegExp("(" + k + ")").exec(fmt);
    if (ret) {
      fmt = fmt.replace(ret[1],
          (ret[1].length == 1) ? (opt[k]) : (opt[k].padStart(ret[1].length,
              "0")))
    }
  }
  return fmt;
}
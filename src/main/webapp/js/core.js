let delegators = [];
let cnstmDelegators = [];
let whereInDelegators = [];
const cnstmAccount = "cnstm";
const whereInAccount = "wherein";

let minDateCnstm = "2018-11-05";
let minDateWhereIn = "2019-01-04";

let queryDate = getQueryDate();
initDateInput();
initData(cnstmAccount, queryDate);
initData(whereInAccount, queryDate);

function getTotalSp(delegators) {
  return new Promise((resolve, reject) => {
    let total = 0;
    for (let i in delegators) {
      total += Number(delegators[i].sp);
    }
    resolve(total);
  });
}

function getDelegators(account, date) {
  return new Promise((resolve, reject) => {
    // let url = window.location.href + "token/summary?account=" + account;
    let url = "http://localhost:8080/token/summary?account=" + account;
    if (isValid(account, date)) {
      url += "&date=" + date;
    }
    axios.get(url).then(function (response) {
      if (response.status == 200) {
        delegators = response.data;
        resolve(delegators);
      }
    });
  });
}

async function initData(account, date) {
  let delegators = await getDelegators(account, date);
  if ("cnstm" == account) {
    cnstmDelegators = delegators;
  } else if ("wherein" == account) {
    whereInDelegators = delegators;
  }
  let totalSp = await getTotalSp(delegators);

  initBanner(account, delegators.length, totalSp.toFixed(2));

  initTable(account, delegators, totalSp.toFixed(2));
}

function initBanner(account, delegatorNum, totalSp) {
  let bannerDiv = $('div#' + account).prev();
  bannerDiv.children()[0].children[0].text = delegatorNum + " Delegator(s)";
  bannerDiv.children()[1].children[0].text = totalSp + " SP";
  bannerDiv.css('display', 'flex');
}

function initTable(account, delegators, totalSp) {
  let totalToken = 0;
  let totalTokenAllDays = 0;
  const tableId = account + "Table";
  let s = '<table id="' + tableId + '" class="sortable">';
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
  s += '<th>Total: </th><th></th><th>' + totalSp
    + ' SP</th><th></th><th>' + totalTokenAllDays.toFixed(2)
    + '</th><th>' + totalToken.toFixed(2) + ' IN</th>';
  s += '</tr></tfoot>';
  s += '</table>';
  $('div#' + account).html(s);
  sorttable.makeSortable(document.getElementById(tableId));
}

/**
 * get the date for querying at current time.
 */
function getQueryDate() {
  let today = new Date();
  let hour = today.getHours();
  if (hour < 8) {
    today.setTime(today.getTime() - 24 * 3600 * 1000 * 2);
  } else {
    today.setTime(today.getTime() - 24 * 3600 * 1000 * 1);
  }
  let day = ("0" + today.getDate()).slice(-2);
  let month = ("0" + (today.getMonth() + 1)).slice(-2);
  let queryDate = today.getFullYear() + "-" + (month) + "-" + (day);
  return queryDate;
}

/**
 * init date input.
 */
function initDateInput() {
  $('.dateInput').val(queryDate);
  $('.dateInput')[0].setAttribute("min", minDateCnstm);
  $('.dateInput')[1].setAttribute("min", minDateWhereIn);
  $('.dateInput').attr('max', queryDate);
}

/**
 * validate whether the date of the account is valid.
 * @param {String} date
 * @param {String} account
 */
function isValid(account, date) {
  if (typeof date == "undefined" || date == null || date == "") {
    return false;
  } else {
    let targetDate = new Date(date);
    let maxDate = new Date(getQueryDate());
    if ("cnstm" == account) {
      if (targetDate >= new Date(minDateCnstm) && targetDate <= maxDate) {
        return true;
      }
      else {
        return false;
      }
    } else if ("wherein" == account) {
      if (targetDate >= new Date(minDateWhereIn) && targetDate <= maxDate) {
        return true;
      }
      else {
        return false;
      }
    }
    else {
      return false;
    }
  }
}

/**
 * query the data of account for the query button.
 * @param {String} account
 */
function query(account) {
  let date;
  if ("cnstm" == account) {
    date = $('.dateInput')[0].value;
  } else if ("wherein" == account) {
    date = $('.dateInput')[1].value;
  }
  initData(account, date);
}

/**
 * snapshot table and download the image.
 * @param {String} account
 */
function snapshot(account) {
  let leftWidth = document.getElementsByClassName("tabs")[0].getBoundingClientRect().left;
  leftWidth = leftWidth > 10 ? leftWidth - 10 : leftWidth;
  const bodyWidth = document.getElementsByClassName("tabs")[0].offsetWidth + 30;
  html2canvas(document.body, { width: bodyWidth, x: leftWidth }).then(function (canvas) {
    var imgUrl = canvas.toDataURL();
    let descriptor = account + "_" + dateFormat("YYYYmmdd_HHMMSS", new Date());
    let imgDownLoad = document.createElement('a');
    imgDownLoad.setAttribute("href", imgUrl);
    imgDownLoad.setAttribute("download", descriptor + ".png");
    imgDownLoad.style.display = "none";

    document.body.appendChild(imgDownLoad);
    imgDownLoad.click();
    document.body.removeChild(imgDownLoad);
  });
}

/**
 * download csv file.
 * @param {String} account
 */
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

  let descriptor = account + "_" + dateFormat("YYYYmmdd_HHMMSS", new Date());
  let csvDownload = document.createElement('a');
  csvDownload.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(dataCSV));
  csvDownload.setAttribute('download', descriptor + ".csv");
  csvDownload.style.display = 'none';

  document.body.appendChild(csvDownload);
  csvDownload.click();
  document.body.removeChild(csvDownload);
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

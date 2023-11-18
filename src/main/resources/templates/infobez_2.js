// 4 вариант N 38 длина бит 123

const randBetween = bigInt.randBetween;
const gcd = bigInt.gcd;
var curN, curS, curE  = undefined;
var loading = document.getElementById("process");
window.addEventListener('load', function () {
    loading.style.display = "none";
    [curE, curS, curN] = getKeys();
});
const blockBytes = 15;
console.log(sizeBlock(curN));
function getPrimeNumber (){
    var randomNumber = randBetween(bigInt(10**18), bigInt("9".repeat(19)));
    if (randomNumber.mod(2).equals(0)) {
        randomNumber = randomNumber.add(1);
    }
    while (true) {
        if (randomNumber.isPrime()) {
            return randomNumber;
        }
        randomNumber = randomNumber.add(2);
    }
}

function getKeys (){
    var q = getPrimeNumber();
    console.log("Q: ".concat(q.toString()));
    var p = getPrimeNumber();
    console.log("P: ".concat(p.toString()));
    var n = p.multiply(q);
    console.log("N: ".concat(n.toString()));
    p = p.minus(1);
    q = q.minus(1);
    var d = p.multiply(q);
    console.log("D: ".concat(d.toString()));
    var s = randBetween(2, d.minus(1));
    while (!gcd(d, s).equals(1)) {
        s = randBetween(2, d.minus(1));
    }
    console.log("S: ".concat(s.toString()));
    var e = findModularInverse(s, d);
    console.log("E: ".concat(e.toString()));
    console.log("E * S mod N  = ".concat(((s.mod(d)).multiply(e.mod(d))).mod(d).toString()));
    return [e, s, n]
}


function findModularInverse(s, n) {
    var [newr, r, t, newt] = [s, n, bigInt(0), bigInt(1)];

    while (newr.compare(0) != 0) {
        var quotient = r.divide(newr);
        [t, newt] = [newt, t.minus((quotient.multiply(newt)))];
        [r, newr] = [newr, r.minus((quotient.multiply(newr)))];
    }

    if (r.compare(1) == 1) {
        return null;
    }

    if (t.compare(0) == -1) {
        t = t.add(n);
    }
    return t;
}

function makeByteBlocks(textBytes){
    var blocks = [];
    for (var i = 0; i < textBytes.length; i += blockBytes){
        blocks.push(textBytes.slice(i, i + blockBytes));
        while (blocks[blocks.length - 1].length < 15){
            const emptyByte = new Uint8Array([0]);
            const bytesWithEmptyByte = new Uint8Array(blocks[blocks.length - 1].length + 1);
            bytesWithEmptyByte.set(blocks[blocks.length - 1]);
            bytesWithEmptyByte.set(emptyByte, blocks[blocks.length - 1].length);
            blocks[blocks.length - 1] = bytesWithEmptyByte;
        }
    }
    return blocks;
}

function makeIntBlocks(intArray){
    var blocks = [];
    for (var i = 0; i < intArray.length; i += blockBytes){
        blocks.push(intArray.slice(i, i + blockBytes));
        while (blocks[blocks.length - 1].length < 15){
            blocks[blocks.length - 1].push(bigInt(0));
        }
    }
    return blocks;
}

function encryptDecryptBlocks(blocks, degree, n){
    var blocksСoded = [];
    blocks.forEach(element => {
        var oneBlock = [];
        element.forEach(el => {
            oneBlock.push(bigInt(el).modPow(degree, n));
        });
        blocksСoded.push(oneBlock);
    });
    return blocksСoded;
}


// Шифрование текста с открытым ключом
function encryptText(plainText, s, n) {
    const textEncoder = new TextEncoder('utf-8');
    const textBytes = textEncoder.encode(plainText);
    return encryptDecryptBlocks(makeByteBlocks(textBytes), s, n);
}

// Расшифрование текста с закрытым ключом
function decryptText(blocksCoded, e, n, decode) {
    const textDecoder = new TextDecoder('utf-8');
    var plainText = "";
    var blocksDecoded = encryptDecryptBlocks(blocksCoded, e, n);
    var bytesBlocks = new Uint8Array(blocksDecoded[0]);
    for (var i = 1; i < blocksDecoded.length; i++){
        var oneBlock = new Uint8Array(blocksDecoded[i]);
        if (i == blocksDecoded.length - 1){
            var lastIndex = blocksDecoded[i].length - 1;
            while (lastIndex >= 0 && blocksDecoded[i][lastIndex] === 0) {
                lastIndex--;
            }
            oneBlock = new Uint8Array(blocksDecoded[i].slice(0, lastIndex + 1));
        }
        var tempArray = new Uint8Array(oneBlock.length + bytesBlocks.length);
        tempArray.set(bytesBlocks, 0);
        tempArray.set(oneBlock, bytesBlocks.length);
        bytesBlocks = tempArray;
    }
    if (decode == true){
        return textDecoder.decode(bytesBlocks);
    } else {
        return bytesBlocks;
    }
}


var bTextEncrypt = document.getElementById("b_textEncrypt");
var  curTextEncrypt, curTextDecrypt  = undefined;
bTextEncrypt.addEventListener("click", function(){
    var text = document.getElementById("textEncrypt").value;
    if (text !== ""){
        if (isUTF8(text)){
            if (curTextEncrypt !== undefined){
                document.getElementById("form_textEncrypt").removeChild(document.getElementById("form_textEncrypt").lastChild);
            }
            loading.style.display = "block";
            curTextEncrypt = text;
            var textCoded = encryptText(curTextEncrypt, curS, curN);

            document.getElementById("textEncrypt").value = "";
            var encryptRow = document.createElement("div");
            encryptRow.classList.add("row");
            var encryptCol = document.createElement("div");
            encryptCol.classList.add("col-4");
            var textNode = document.createTextNode("Введённый текст: ");
            var textBefore = document.createElement("div");
            textBefore.appendChild(document.createTextNode(String(curTextEncrypt)));
            textBefore.classList.add("scroll");
            document.getElementById("form_textEncrypt").appendChild(encryptRow);
            encryptRow.appendChild(encryptCol);
            encryptCol.appendChild(textNode);
            encryptCol.appendChild(textBefore);
            encryptCol.appendChild(document.createElement("br"));
            var textNode2 = document.createTextNode("Зашифрованный текст: ");
            var textAfter = document.createElement("div");
            textAfter.appendChild(document.createTextNode(textCoded));
            textAfter.classList.add("scroll");
            encryptCol.appendChild(textNode2);
            encryptCol.appendChild(textAfter);
            loading.style.display = "none";
        } else {
            alert("Введённые символы не соответсвуют кодировке UTF-8!");
        };
    } else {
        alert("Пожалуйста, введите текст!");
    };
}, false);

var bTextDecrypt = document.getElementById("b_textDecrypt");
bTextDecrypt.addEventListener("click", function(){
    var text = document.getElementById("textDecrypt").value;
    if (text !== ""){
        if (isUTF8(text)){
            if (checkNumbers(document.getElementById('textDecrypt').value)){
                if (curTextDecrypt !== undefined){
                    document.getElementById("form_textDecrypt").removeChild(document.getElementById("form_textDecrypt").lastChild);
                }
                loading.style.display = "block";
                curTextDecrypt = makeIntBlocks(text.split(/,/).map(el => bigInt(el)));
                var textEncoded = decryptText(curTextDecrypt, curE, curN, true);

                document.getElementById("textDecrypt").value = "";
                var decryptRow = document.createElement("div");
                decryptRow.classList.add("row");
                var decryptCol = document.createElement("div");
                decryptCol.classList.add("col-4");
                var textNode = document.createTextNode("Введённый текст: ");
                var textBefore = document.createElement("div");
                textBefore.appendChild(document.createTextNode(String(curTextDecrypt)));
                textBefore.classList.add("scroll");
                document.getElementById("form_textDecrypt").appendChild(decryptRow);
                decryptRow.appendChild(decryptCol);
                decryptCol.appendChild(textNode);
                decryptCol.appendChild(textBefore);
                decryptCol.appendChild(document.createElement("br"));
                var textNode2 = document.createTextNode("Расшифрованный текст: ");
                var textAfter = document.createElement("div");
                textAfter.appendChild(document.createTextNode(textEncoded));
                textAfter.classList.add("scroll");
                decryptCol.appendChild(textNode2);
                decryptCol.appendChild(textAfter);
                loading.style.display = "none";

            } else {
                alert("Некорректный ввод! Введите числа через запятую.");
            };
        } else {
            alert("Введённые символы не соответсвуют кодировке UTF-8!");
        };
    } else {
        alert("Пожалуйста, введите текст!");
    };
}, false);

function isUTF8(text) {
    try {
        new TextDecoder('utf-8').decode(new TextEncoder('utf-8').encode(text));
        return true;
    } catch (error) {
        return false;
    }
}

function isValidNumber(num) {
    try {
        var parsed = bigInt(num);
    } catch (error) {
        return false;
    }
    return parsed.compare(0) >= 0 && parsed.compare(curN) < 0;
}

function checkNumbers(numbersString) {
    const numbersArray = numbersString.split(/,/).map(num => num.trim());
    const allValid = numbersArray.every(num => isValidNumber(num));
    return allValid;
}

document.getElementById('fileEncrypt').addEventListener('submit', function (event) {
    event.preventDefault(); // Предотвратить стандартное поведение отправки формы (обновление страницы)

    fetch('/fileEncrypt', {
      method: 'POST',
      body: new FormData(this) // Отправить данные формы
    }).then(response => {
        if (response.ok) {
            // Файл успешно отправлен на сервер
            console.log('Файл успешно отправлен.');
        } else {
            console.error('Произошла ошибка при отправке файла.');
        }
    }).catch(error => {
            console.error('Ошибка при отправке файла:', error);
        });
});
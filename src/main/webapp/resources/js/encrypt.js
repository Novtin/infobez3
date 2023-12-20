
async function encrypt(message, secretKeyBytes) {
    while (new TextEncoder().encode(secretKeyBytes).length % 16 !== 0) {
        secretKeyBytes = "0" + secretKeyBytes;
    }
    console.log(secretKeyBytes);

    const key = CryptoJS.enc.Utf8.parse(secretKeyBytes);
    const iv = CryptoJS.enc.Utf8.parse("0000000000000000");

    const encrypted = CryptoJS.AES.encrypt(message, key, {
        iv: iv,
        mode: CryptoJS.mode.CBC,
        padding: CryptoJS.pad.Pkcs7
    });

    return encrypted.toString();
}
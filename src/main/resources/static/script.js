const form = document.getElementById("crawlForm");
const addUrlBtn = document.getElementById("addUrlBtn");
const urlContainer = document.getElementById("urlContainer");
const resultsDiv = document.getElementById("results");
const loader = document.getElementById("loader");
const statusDiv = document.getElementById("status");

let urlCount = 1;
const MAX_URLS = 5;

addUrlBtn.addEventListener("click", () => {

    if (urlCount >= MAX_URLS) return;

    urlCount++;

    const div = document.createElement("div");
    div.className = "input-group";

    div.innerHTML = `
        <input type="url"
               name="url${urlCount}"
               placeholder="https://example.com" />
        <button type="button" class="remove-btn">✕</button>
    `;

    urlContainer.appendChild(div);

    if (urlCount >= MAX_URLS) {
        addUrlBtn.disabled = true;
    }

    div.querySelector(".remove-btn").addEventListener("click", () => {
        div.remove();
        urlCount--;
        addUrlBtn.disabled = false;
    });
});

/* -------------------------
   Submit Handler
-------------------------- */
form.addEventListener("submit", async (e) => {
    e.preventDefault();

    resultsDiv.innerHTML = "";
    statusDiv.textContent = "";
    loader.classList.remove("hidden");

    const formData = new FormData(form);

    try {
        const response = await fetch("/crawl/start", {
            method: "POST",
            body: new URLSearchParams(formData)
        });

        const data = await response.json();
        loader.classList.add("hidden");

        statusDiv.innerHTML = `
            <div class="card fade-in">
                <strong>Pages:</strong> ${data.totalPages}<br>
                <strong>Time:</strong> ${data.crawlTime} ms
            </div>
        `;

        data.result.forEach((page, index) => {

            const card = document.createElement("div");
            card.className = "card fade-in";
            card.style.animationDelay = `${index * 0.1}s`;

            card.innerHTML = `
                <div class="url">${page.url}</div>
                <div class="title">${page.title}</div>
                <div>Words: ${page.wordCount}</div>
                <div class="snippet">${page.content}</div>
            `;

            resultsDiv.appendChild(card);
        });

    } catch (err) {
        loader.classList.add("hidden");
        statusDiv.textContent = "Error: " + err.message;
    }
});

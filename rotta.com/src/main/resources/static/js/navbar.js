document.querySelectorAll(".app-navbar").forEach((navbar) => {
    const toggle = navbar.querySelector(".app-nav-toggle");
    const menu = navbar.querySelector(".app-nav-menu");
    const account = navbar.querySelector(".app-account-menu");
    if (!toggle || !menu || navbar.dataset.enhanced) return;
    navbar.dataset.enhanced = "true";

    function setOpen(open) {
        menu.classList.toggle("is-open", open);
        toggle.setAttribute("aria-expanded", String(open));
        toggle.setAttribute("aria-label", open ? "Fechar menu de navegação" : "Abrir menu de navegação");
        if (!open && account) account.open = false;
    }

    toggle.addEventListener("click", () => setOpen(toggle.getAttribute("aria-expanded") !== "true"));
    navbar.addEventListener("keydown", (event) => {
        if (event.key === "Escape") {
            setOpen(false);
            toggle.focus();
        }
    });
    document.addEventListener("click", (event) => {
        if (!navbar.contains(event.target)) setOpen(false);
    });
});

(function () {
    var root = document.getElementById('sidebar-root');
    if (!root) return;

    var links = [
        { href: 'notes.html',        label: 'Notas',     icon: 'bi-journal-text' },
        { href: 'mood-history.html', label: 'Humor',     icon: 'bi-graph-up' },
        { href: 'dashboard.html',    label: 'Dashboard', icon: 'bi-fire' },
        { href: 'agenda.html',       label: 'Agenda',    icon: 'bi-calendar3' },
        { href: 'water.html',        label: 'Água',      icon: 'bi-droplet-fill' }
    ];

    var current = window.location.pathname.split('/').pop() || '';

    function desktopLinks() {
        return links.map(function (l) {
            var active = (l.href === current) ? ' active' : '';
            return '<li>'
                +    '<a class="sidebar-link' + active + '" href="' + l.href + '">'
                +      '<i class="bi ' + l.icon + '"></i>'
                +      '<span class="sidebar-label">' + l.label + '</span>'
                +    '</a>'
                +  '</li>';
        }).join('');
    }

    function mobileLinks() {
        return links.map(function (l) {
            var active = (l.href === current) ? ' active' : '';
            return '<a class="mobile-nav-link' + active + '" href="' + l.href + '">'
                +    '<i class="bi ' + l.icon + '"></i><span>' + l.label + '</span>'
                +  '</a>';
        }).join('');
    }

    root.innerHTML =
        '<aside class="sidebar d-none d-lg-flex" aria-label="Navegação principal">'
        +   '<span class="sidebar-brand" title="Meu Diário">'
        +     '<i class="bi bi-journal-text"></i>'
        +     '<span class="sidebar-label">Meu Diário</span>'
        +   '</span>'
        +   '<ul class="sidebar-nav">' + desktopLinks() + '</ul>'
        +   '<div class="sidebar-spacer"></div>'
        +   '<button class="sidebar-link sidebar-logout" id="logout-button" type="button">'
        +     '<i class="bi bi-box-arrow-right"></i>'
        +     '<span class="sidebar-label">Sair</span>'
        +   '</button>'
        + '</aside>'
        + '<nav class="mobile-topbar d-flex d-lg-none">'
        +   '<span class="mobile-brand"><i class="bi bi-journal-text"></i> Meu Diário</span>'
        +   '<button class="mobile-toggler" type="button" data-bs-toggle="collapse" '
        +     'data-bs-target="#mobileNavLinks" aria-controls="mobileNavLinks" '
        +     'aria-expanded="false" aria-label="Abrir menu">'
        +     '<i class="bi bi-list"></i>'
        +   '</button>'
        + '</nav>'
        + '<div class="collapse mobile-collapse d-lg-none" id="mobileNavLinks">'
        +   mobileLinks()
        +   '<button class="mobile-nav-link mobile-logout" id="mobile-logout-button" type="button">'
        +     '<i class="bi bi-box-arrow-right"></i><span>Sair</span>'
        +   '</button>'
        + '</div>';

    document.body.classList.add('has-sidebar');

    // Delega no #logout-button real para não duplicar/alterar o listener
    // que cada página já anexa no fim do body.
    var mobileLogout = document.getElementById('mobile-logout-button');
    if (mobileLogout) {
        mobileLogout.addEventListener('click', function () {
            var real = document.getElementById('logout-button');
            if (real) real.click();
        });
    }
})();

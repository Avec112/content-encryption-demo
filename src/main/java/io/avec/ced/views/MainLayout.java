package io.avec.ced.views;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import io.avec.ced.data.entity.Manager;
import io.avec.ced.security.AuthenticatedUser;
import io.avec.ced.views.manager.ManagerView;
import io.avec.ced.views.superhero.SuperHeroView;
import io.avec.ced.views.superhero.SuperheroManagerView;
import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The main view is a top-level placeholder for other views.
 */
@RequiredArgsConstructor
@PageTitle("Main")
public class MainLayout extends AppLayout {

    // java 17
    public record MenuItemInfo(String text,
                               String iconClass,
                               Class<? extends Component> view) {}

    // java < 17
//    @Getter
//    @RequiredArgsConstructor
//    public static class MenuItemInfo {
//        private final String text;
//        private final String iconClass;
//        private final Class<? extends Component> view;
//    }

    private H1 viewTitle;

    private final AuthenticatedUser authenticatedUser;
    private final AccessAnnotationChecker accessChecker;


    @PostConstruct
    private void init() {
        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        addToDrawer(createDrawerContent());
    }

    private Component createHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassName("text-secondary");
        toggle.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames("m-0", "text-l");


        final HorizontalLayout userLayout = managerLayout();
        userLayout.getStyle().set("margin-left", "auto").set("padding-right", "10px");
        Header header = new Header(toggle, viewTitle, userLayout);
        header.addClassNames("bg-base", "border-b", "border-contrast-10", "box-border", "flex", "h-xl", "items-center",
                "w-full");
        return header;
    }

    private HorizontalLayout managerLayout() {
        HorizontalLayout layout = new HorizontalLayout();

        Optional<Manager> maybeManager = authenticatedUser.get();
        if (maybeManager.isPresent()) {
            Manager manager = maybeManager.get();

            Avatar avatar = new Avatar(manager.getName(), manager.getProfilePictureUrl());
            avatar.addClassNames("me-xs");

            ContextMenu managerMenu = new ContextMenu(avatar);
            managerMenu.setOpenOnClick(true);
            managerMenu.addItem("Logout", e -> authenticatedUser.logout());

            Span name = new Span(manager.getName());
            name.addClassNames("font-medium", "text-s", "text-secondary");

            layout.add(avatar, name);
        } else {
            Anchor loginLink = new Anchor("login", "Sign in");
            layout.add(loginLink);
        }
        return layout;
    }

    private Component createDrawerContent() {
        H2 appName = new H2("My App");
        appName.addClassNames("flex", "items-center", "h-xl", "m-0", "px-m", "text-m");

        com.vaadin.flow.component.html.Section section = new com.vaadin.flow.component.html.Section(appName,
                createNavigation()/*, createFooter()*/);
        section.addClassNames("flex", "flex-col", "items-stretch", "max-h-full", "min-h-full");
        return section;
    }

    private Nav createNavigation() {
        Nav nav = new Nav();
        nav.addClassNames("border-b", "border-contrast-10", "flex-grow", "overflow-auto");
        nav.getElement().setAttribute("aria-labelledby", "views");

        H3 views = new H3("Views");
        views.addClassNames("flex", "h-m", "items-center", "mx-m", "my-0", "text-s", "text-tertiary");
        views.setId("views");

        // Wrap the links in a list; improves accessibility
        UnorderedList list = new UnorderedList();
        list.addClassNames("list-none", "m-0", "p-0");
        nav.add(list);

        for (RouterLink link : createLinks()) {
            ListItem item = new ListItem(link);
            list.add(item);
        }
        return nav;
    }

    private List<RouterLink> createLinks() {
        MenuItemInfo[] menuItems = new MenuItemInfo[]{ //
                new MenuItemInfo("Manager", "la la-user", ManagerView.class),
                new MenuItemInfo("Superhero", "la la-columns", SuperHeroView.class),
                new MenuItemInfo("Superhero Managers", "la la-lock", SuperheroManagerView.class)

        };
        List<RouterLink> links = new ArrayList<>();
        for (MenuItemInfo menuItemInfo : menuItems) {
            if (accessChecker.hasAccess(menuItemInfo.view())) {
                links.add(createLink(menuItemInfo));
            }

        }
        return links;
    }

    private static RouterLink createLink(MenuItemInfo menuItemInfo) {
        RouterLink link = new RouterLink();
        link.addClassNames("flex", "mx-s", "p-s", "relative", "text-secondary");
        link.setRoute(menuItemInfo.view());

        Span icon = new Span();
        icon.addClassNames("me-s", "text-l");
        if (!menuItemInfo.iconClass().isEmpty()) {
            icon.addClassNames(menuItemInfo.iconClass());
        }

        Span text = new Span(menuItemInfo.text());
        text.addClassNames("font-medium", "text-s");

        link.add(icon, text);
        return link;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}

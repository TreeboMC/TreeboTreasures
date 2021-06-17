package me.shakeforprotein.treebotreasures.HelpBook;

import me.shakeforprotein.treebotreasures.TreeboTreasures;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class HelpBook {

    private TreeboTreasures pl;
    private ArrayList<BaseComponent[]> pages = new ArrayList<>();
    private int counter = 2;

    public HelpBook(TreeboTreasures main){
        this.pl = main;
        pages.add(createTitlePage("Treebo Treasures"));
        pages.add(createTableOfContents());
        pages.addAll(createOtherPages());
        pl.roots.helpHandler.registerHelpBook("TreeboTreasures", "TreeboMC", pages);

        pl.roots.commandsGui.registerPlugin(pl, "openHelpBook-TreeboTreasures", new ItemStack(Material.ENDER_CHEST, 1), ChatColor.translateAlternateColorCodes('&', "Treebo Treasures Help"), getLoreList());
    }

    private BaseComponent[] createTitlePage(String title){

        TextComponent titlePage = new TextComponent(title);
        titlePage.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);
        titlePage.setUnderlined(true);

        BaseComponent[] titlePageComponent = new ComponentBuilder(titlePage).create();
        return titlePageComponent;
    }

    private BaseComponent[] createTableOfContents(){
        int i = counter;
        TextComponent textComponent = new TextComponent(ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "Contents\n");

        //Add TOC Link lines
        TextComponent newText = new TextComponent(ChatColor.DARK_BLUE + "/DailyReward (Hub Only)\n");
        i = i+1;
        newText.setClickEvent(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, i + ""));
        textComponent.addExtra(newText);

        newText = new TextComponent(ChatColor.DARK_BLUE + "/ShowRewards\n");
        i = i+1;
        newText.setClickEvent(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, i + ""));
        textComponent.addExtra(newText);

        newText = new TextComponent(ChatColor.DARK_BLUE + "/TreasuresGui\n");
        i = i+1;
        newText.setClickEvent(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, i + ""));
        textComponent.addExtra(newText);


        //create TOC page
        BaseComponent[] tableOfContents= new ComponentBuilder(textComponent).create();
        counter = i;
        return tableOfContents;
    }



    private List<BaseComponent[]> createOtherPages(){

        List<BaseComponent[]> pageList = new ArrayList<>();

        //add command help page.
        pageList.add(new ComponentBuilder("/DailyRewards\n\nOpens the key claim GUI to claim your daily reward key in hub.").create());
        pageList.add(new ComponentBuilder("/ShowRewards\n\nOpens a gui showing all possible rewards from the reward keys.\n\nUsage: /ShowRewards common\n/ShowRewards legendary").create());
        pageList.add(new ComponentBuilder("/TreasuresGUI\n\nOpens the treasure claim gui to spend your reward keys.").create());

        return pageList;
    }

    private List<String> getLoreList(){
        List<String> loreList = new ArrayList<>();

        loreList.add(ChatColor.translateAlternateColorCodes('&', pl.badge + "-Help"));
        loreList.add(ChatColor.translateAlternateColorCodes('&', ""));
        loreList.add(ChatColor.translateAlternateColorCodes('&', "This book will provide more detailed"));
        loreList.add(ChatColor.translateAlternateColorCodes('&', "help on Treebo's Treasures Key Reward commands"));

        return loreList;
    }
}

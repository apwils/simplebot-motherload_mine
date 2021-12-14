package mlm;

import simple.hooks.filters.SimpleBank;
import simple.hooks.scripts.Category;
import simple.hooks.scripts.ScriptManifest;
import simple.hooks.simplebot.ChatMessage;
import simple.hooks.wrappers.SimpleLocalPlayer;
import simple.hooks.wrappers.SimpleObject;
import simple.robot.script.Script;
import net.runelite.api.Player;
import java.awt.*;

import net.runelite.api.coords.WorldPoint;
import simple.robot.utils.WorldArea;

@ScriptManifest(author = "Apwils", name = "Apwils | Motherload Mine", category = Category.MINING, version = "1.0",
        description = "Start at bank with pickaxe equipped.", discord = "Andrew w#0376", servers = { "Zaros" })

public class Mlm extends Script {

    private static WorldPoint bankLocation = new WorldPoint(3760, 5666, 0);
    private static WorldPoint miningLocation = new WorldPoint(3727, 5678, 0);
    private static WorldPoint hopperLocation = new WorldPoint(3750, 5673, 0);
    private static WorldPoint sackLocation = new WorldPoint(3748, 5659, 0);

    private int runs;
    private String status;
    private long startTime;

    @Override
    public void onExecute() {
        startTime = System.currentTimeMillis();
        status = "Waiting to start...";
        runs =  0;
    }

    @Override
    public void onProcess() {
        if(status == "Banking..." || status == "Walking to bank location"){
            // bank ores
            if (ctx.players.getLocal().getLocation().distanceTo(bankLocation) > 3) {
                status = "Walking to bank location";
                ctx.pathing.step(bankLocation);
            } else {
                status = "Banking...";
                final SimpleObject bank = (SimpleObject) ctx.objects.populate().filter(26707).nearest().next();
                if(bank.click(0)){
                    ctx.sleep(1000);

                    ctx.bank.depositInventory();
                    ctx.sleep(300);
                    runs ++;
                    status = "Walking to mining location";
                }
            }
        } else {
            if (ctx.inventory.populate().size() == 28) {
                if (ctx.players.getLocal().getLocation().distanceTo(hopperLocation) > 3) {
                    status = "Walking to Hopper";
                    ctx.pathing.step(hopperLocation);
                } else {
                    final SimpleObject hopper = (SimpleObject) ctx.objects.populate().filter(26674).nearest().next();
                    status = "Depositing dirt";
                    if (hopper.click(0)) {
                        runs++;
                        ctx.sleep(300);

                    }
                }
            } else {

                if (runs != 0 && runs % 5 == 0) {
                    // collect from sack
                    if (ctx.players.getLocal().getLocation().distanceTo(sackLocation) > 3) {
                        status = "Walking to sack location";
                        ctx.pathing.step(sackLocation);
                    } else {
                        ctx.sleep(4000);
                        final SimpleObject sack = (SimpleObject) ctx.objects.populate().filter(26686).nearest().next();
                        status = "Collecting from sack";

                        if (sack.click(0)) {
                            ctx.sleep(300);
                            status = "Banking...";
                        }
                    }
                } else {
                    if (ctx.players.getLocal().getLocation().distanceTo(miningLocation) > 10) {
                        status = "Walking to mining location";
                        ctx.pathing.step(miningLocation);
                    } else {

                        final SimpleLocalPlayer player = (SimpleLocalPlayer) ctx.players.getLocal();
                        if (!player.isAnimating() && !ctx.pathing.inMotion()) {
                            final SimpleObject vein = (SimpleObject) ctx.objects.populate().filter(26662, 26663, 26664).filter(
                                    new WorldPoint(3730, 5679, 0),
                                    new WorldPoint(3730, 5678, 0),
                                    new WorldPoint(3730, 5677, 0),
                                    new WorldPoint(3725, 5674, 0),
                                    new WorldPoint(3725, 5675, 0),
                                    new WorldPoint(3728, 5675, 0),
                                    new WorldPoint(3728, 5674, 0),
                                    new WorldPoint(3727, 5672, 0),
                                    new WorldPoint(3727, 5671, 0)
                            ).nearest().next();
                            if (vein.click(0)) {
                                status = "Mining vein";
                                ctx.sleep(1000);

                            }
                        }
                    }


                }
            }
        }
    }

    @Override
    public void onTerminate() {

    }

    @Override
    public void onChatMessage(ChatMessage chatMessage) {

    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(5, 2, 192, 72);

        g.setColor(Color.decode("#303030"));
        g.drawRect(5, 2, 192, 72);

        g.setColor(Color.decode("#4d4d4d"));
        g.drawLine(8, 24, 194, 24);

        g.setColor(Color.decode("#3150ad"));
        g.drawString("v. " + "1.0", 160, 20);

        g.setColor(Color.decode("#787878"));
        g.drawString("Apwils | Motherload Mine", 12, 20);
        g.drawString("Time: " + ctx.paint.formatTime(System.currentTimeMillis() - startTime), 14, 42);
        g.drawString("Status: " + status, 14, 56);
        g.drawString("Runs: " + runs + " (" + ctx.paint.valuePerHour(runs, startTime) + ")", 14, 70);

    }


}
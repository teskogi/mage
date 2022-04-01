
package org.mage.test.cards.single.c20;

import mage.constants.PhaseStep;
import mage.constants.Zone;
import org.junit.Ignore;
import org.mage.test.serverside.base.CardTestPlayerBase;
import org.junit.Test;


public class SouvenirSnatcherTest extends CardTestPlayerBase {
    
    @Test
    public void test_SouvenirSnatcher_Cast() {
        
        addCard(Zone.HAND, playerA, "Souvenir Snatcher", 1); // {4}{U}
        addCard(Zone.BATTLEFIELD, playerB, "Chromatic Star", 1);
        addCard(Zone.BATTLEFIELD, playerA, "Island", 5); 
        
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Souvenir Snatcher");
        
        //assert no choises were made

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();
        assertAllCommandsUsed();

        assertPermanentCount(playerB, "Chromatic Star", 1);
    }
    
    @Test
    public void test_SouvenirSnatcher_Mutate() {
        
        addCard(Zone.HAND, playerA, "Souvenir Snatcher", 1); // {5}{U} mutate
        addCard(Zone.BATTLEFIELD, playerB, "Chromatic Star", 1);
        addCard(Zone.BATTLEFIELD, playerA, "Island", 6); 
        addCard(Zone.BATTLEFIELD, playerA, "Grizzly Bears", 1); 
        
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Souvenir Snatcher with mutate");
        setChoice(playerA, "Grizzly Bears"); // target of mutate
        setChoice(playerA, false); // on top
        addTarget(playerA, "Chromatic Star"); // target for Souvenir Snatcher

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();
        assertAllCommandsUsed();

        assertTappedCount("Island", true, 6);
        assertPermanentCount(playerA, "Chromatic Star", 1);
        assertPermanentCount(playerA, "Souvenir Snatcher", 0);
    }
    
    @Test
    public void test_SouvenirSnatcher_Exile() {
        
        addCard(Zone.HAND, playerA, "Souvenir Snatcher", 1); // {5}{U} mutate
        addCard(Zone.HAND, playerA, "Resculpt", 1); // {1}{U} Exile target artifact or creature. Its controller creates a 4/4 blue and red Elemental creature token.
        addCard(Zone.BATTLEFIELD, playerB, "Chromatic Star", 1);
        addCard(Zone.BATTLEFIELD, playerA, "Island", 8); 
        addCard(Zone.BATTLEFIELD, playerA, "Grizzly Bears", 1); 
        
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Souvenir Snatcher with mutate");
        setChoice(playerA, "Grizzly Bears"); // target of mutate
        setChoice(playerA, false); // on top
        addTarget(playerA, "Chromatic Star"); // target for Souvenir Snatcher
        waitStackResolved(1, PhaseStep.PRECOMBAT_MAIN);
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Resculpt", "Grizzly Bears"); // Flicker the mutated creature
        
        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();
        assertAllCommandsUsed();
        
        assertPermanentCount(playerA, "Chromatic Star", 1);
        assertExileCount(playerA, "Souvenir Snatcher", 1);
        assertExileCount(playerA, "Grizzly Bears", 1);
    }
    
    @Test
    @Ignore
    public void test_SouvenirSnatcher_Flicker() {
        
        addCard(Zone.HAND, playerA, "Souvenir Snatcher", 1); // {5}{U} mutate
        addCard(Zone.HAND, playerA, "Essence Flux", 1); // {U} Exile target creature you control, then return that card to the battlefield under its owner's control. If it's a Spirit, put a +1/+1 counter on it.
        addCard(Zone.BATTLEFIELD, playerB, "Chromatic Star", 1);
        addCard(Zone.BATTLEFIELD, playerA, "Island", 7); 
        addCard(Zone.BATTLEFIELD, playerA, "Grizzly Bears", 1); 
        
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Souvenir Snatcher with mutate");
        setChoice(playerA, "Grizzly Bears"); // target of mutate
        setChoice(playerA, false); // on top
        addTarget(playerA, "Chromatic Star"); // target for Souvenir Snatcher
        waitStackResolved(1, PhaseStep.PRECOMBAT_MAIN);
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Essence Flux", "Grizzly Bears"); // Flicker the mutated creature
        
        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();
        assertAllCommandsUsed();
        
        assertPermanentCount(playerA, "Chromatic Star", 1);
        assertPermanentCount(playerA, "Grizzly Bears", 1);
        assertPermanentCount(playerA, "Souvenir Snatcher", 1);
    }
}


package org.mage.test.cards.single.iko;

import mage.constants.PhaseStep;
import mage.constants.Zone;
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
    
}

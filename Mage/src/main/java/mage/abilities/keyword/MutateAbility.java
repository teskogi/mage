package mage.abilities.keyword;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import mage.MageObject;
import mage.abilities.Ability;
import mage.abilities.SpellAbility;
import mage.abilities.common.MutatesSourceTriggeredAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.Effect;
import mage.abilities.effects.ReplacementEffectImpl;
import mage.cards.Card;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.SpellAbilityType;
import mage.constants.SubType;
import mage.constants.SuperType;
import mage.constants.Zone;
import mage.filter.common.FilterCreaturePermanent;
import mage.filter.predicate.Predicates;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.events.GameEvent.EventType;
import mage.game.permanent.Permanent;
import mage.game.permanent.PermanentCard;
import mage.game.permanent.PermanentToken;
import mage.players.Player;
import mage.target.TargetPermanent;
import mage.target.common.TargetCreaturePermanent;

public class MutateAbility extends SpellAbility {

    private static final FilterCreaturePermanent filter = new FilterCreaturePermanent("non-Human creature");

    static {
        filter.add(CardType.CREATURE.getPredicate());
        filter.add(Predicates.not(SubType.HUMAN.getPredicate()));
    }
    
    public MutateAbility(Card card, String manaString) {
        super(card.getSpellAbility());
        this.newId();
        this.setCardName(card.getName() + " with mutate");
        zone = Zone.HAND;
        spellAbilityType = SpellAbilityType.BASE_ALTERNATE;

        this.getManaCosts().clear();
        this.getManaCostsToPay().clear();
        this.addManaCost(new ManaCostsImpl(manaString));

        this.setRuleAtTheTop(true);

        Ability ability = new SimpleStaticAbility(Zone.BATTLEFIELD, new MutateEntersBattlefieldEffect());
        ability.setRuleVisible(false);
        addSubAbility(ability);
        
    }

    private MutateAbility(final MutateAbility ability) {
        super(ability);
    }

    @Override
    public MutateAbility copy() {
        return new MutateAbility(this);
    }

    @Override
    public String getRule(boolean all) {
        return getRule();
    }

    @Override
    public String getRule() {
        return "Mutate " + getManaCostsToPay().getText() + " <i>(If you cast this spell for its mutate cost, " +
                "put it over or under target non-Human creature you own. " +
                "They mutate into the creature on top plus all abilities from under it.)</i>";
    }

    @Override
    public boolean activate(Game game, boolean noMana) {
        MageObject mutateObject = game.getBaseObject(this.getSourceId());
        Player controller = game.getPlayer(this.getControllerId());
        if (controller != null) {
            TargetPermanent target = new TargetCreaturePermanent(filter);
            //get the non-human creature we are mutating onto
            if (controller.choose(Outcome.BoostCreature, target, this, game)) {
                Permanent targetCreature = game.getPermanent(target.getFirstTarget());
                
                if (targetCreature != null) {
                    //get whether the entering permanent is on top
                    boolean onTop = controller.chooseUse(Outcome.Neutral, "Entering "+mutateObject.getName()+" on top?", this, game);
                    for(Ability ability : subAbilities){
                        if (ability instanceof SimpleStaticAbility){
                            for(Effect effect: ability.getEffects()){
                                if (effect instanceof MutateEntersBattlefieldEffect){
                                    ((MutateEntersBattlefieldEffect) effect).setNext(this.getControllerId(), targetCreature, onTop);
                                    return super.activate(game, noMana);
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
}

class MutateEntersBattlefieldEffect extends ReplacementEffectImpl {

    private static final FilterCreaturePermanent filter = new FilterCreaturePermanent("non-Human creature");
    
    private static Map<UUID, ArrayList<Object>> nextTarget = new HashMap<>();
    
    static {
        filter.add(CardType.CREATURE.getPredicate());
        filter.add(Predicates.not(SubType.HUMAN.getPredicate()));
    }
    
    public MutateEntersBattlefieldEffect() {
        super(Duration.WhileOnBattlefield, Outcome.Neutral);
    }

    public MutateEntersBattlefieldEffect(final MutateEntersBattlefieldEffect effect) {
        super(effect);
    }

    @Override
    public boolean checksEventType(GameEvent event, Game game) {
        return EventType.ENTERS_THE_BATTLEFIELD_SELF == event.getType();
    }

    @Override
    public boolean applies(GameEvent event, Ability source, Game game) {
        return event.getTargetId().equals(source.getSourceId());
    }

    @Override
    public boolean replaceEvent(GameEvent event, Ability source, Game game) {
        Permanent mutatePermanent = game.getPermanentEntering(source.getSourceId());
        ArrayList<Object> targetAndOnTop = nextTarget.remove(source.getControllerId());
        //if we have a target creature, we are mutating, otherwise we are being cast normally
        if (mutatePermanent != null && targetAndOnTop != null) {
            Permanent targetCreature = (Permanent) targetAndOnTop.get(0);
            if((boolean) targetAndOnTop.get(1)){
                targetCreature.setName(mutatePermanent.getName());
                targetCreature.getColor(game).setColor(mutatePermanent.getColor(game));
                targetCreature.getManaCost().clear();
                targetCreature.getManaCost().add(mutatePermanent.getManaCost());
                targetCreature.getCardType().clear();
                for (CardType type : mutatePermanent.getCardType()) {
                    targetCreature.addCardType(type);
                }
                targetCreature.getSubtype(game).clear();
                for (SubType type : mutatePermanent.getSubtype(game)) {
                    targetCreature.getSubtype(game).add(type);
                }
                targetCreature.getSuperType().clear();
                for (SuperType type : mutatePermanent.getSuperType()) {
                    targetCreature.addSuperType(type);
                }

                // to get the image of the copied permanent copy number and expansionCode
                if (mutatePermanent instanceof PermanentCard) {
                    targetCreature.setCardNumber(((PermanentCard) mutatePermanent).getCard().getCardNumber());
                    targetCreature.setExpansionSetCode(((PermanentCard) mutatePermanent).getCard().getExpansionSetCode());
                } else if (mutatePermanent instanceof PermanentToken || mutatePermanent instanceof Card) {
                    targetCreature.setCardNumber(((Card) mutatePermanent).getCardNumber());
                    targetCreature.setExpansionSetCode(((Card) mutatePermanent).getExpansionSetCode());
                }
            } 

            for (Ability ability : mutatePermanent.getAbilities()) {
                targetCreature.addAbility(ability, source.getSourceId(), game, false);
            }
            targetCreature.addMergedCard(mutatePermanent.getId());
            for (Ability ability : targetCreature.getAbilities()) {
                if(ability instanceof MutatesSourceTriggeredAbility){
                    ((MutatesSourceTriggeredAbility) ability).trigger(game, source.getControllerId(), event);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public MutateEntersBattlefieldEffect copy() {
        return new MutateEntersBattlefieldEffect(this);
    }
        
    public void setNext(UUID owner, Permanent targetCreature, boolean onTop){
        ArrayList<Object> targetAndOnTop = new ArrayList<Object>();
        targetAndOnTop.add(targetCreature);
        targetAndOnTop.add(onTop);
        nextTarget.remove(owner);
        nextTarget.put(owner, targetAndOnTop);
    }
    
    
}

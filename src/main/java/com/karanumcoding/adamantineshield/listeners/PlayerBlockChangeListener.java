package com.karanumcoding.adamantineshield.listeners;

import java.util.Date;
import java.util.UUID;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.First;

import com.karanumcoding.adamantineshield.db.Database;
import com.karanumcoding.adamantineshield.db.queue.BlockQueueEntry;
import com.karanumcoding.adamantineshield.enums.ActionType;

public class PlayerBlockChangeListener {
	
	private Database db;
	
	public PlayerBlockChangeListener(Database db) {
		this.db = db;
	}
	
	@Listener(order = Order.POST)
	public void onBlockPlace(ChangeBlockEvent.Place e, @First Player p) {
		for (Transaction<BlockSnapshot> transaction : e.getTransactions()) {
			UUID id = p.getUniqueId();
			if (transaction.getOriginal().getState().getType() != BlockTypes.AIR) {
				db.addToQueue(new BlockQueueEntry(transaction.getOriginal(), ActionType.DESTROY, id.toString(), new Date().getTime()));
			}
			db.addToQueue(new BlockQueueEntry(transaction.getFinal(), ActionType.PLACE, id.toString(), new Date().getTime()));
		}
	}
	
	@Listener(order = Order.POST)
	public void onBlockBreak(ChangeBlockEvent.Break e, @First Player p) {
		for (Transaction<BlockSnapshot> transaction : e.getTransactions()) {
			db.addToQueue(new BlockQueueEntry(transaction.getOriginal(), ActionType.DESTROY, p.getUniqueId().toString(), new Date().getTime()));
		}
	}
	
}
